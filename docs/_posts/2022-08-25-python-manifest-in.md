---
layout: post
title: Including Files in Source Distributions with MANIFEST.in
tags: [Python]
color: rgb(59, 118, 167)
feature-img: "assets/img/post-cover/36-cover.png"
thumbnail: "assets/img/post-cover/36-cover.png"
author: QubitPi
excerpt_separator: <!--more-->
---

Often packages will need to depend on files which are not .py files: e.g. images, data tables, documentation, etc. Those 
files need special treatment in order for setuptools to handle them correctly. The mechanism that provides this is the 
MANIFEST.in file.

<!--more-->

When building a
[source distribution](https://packaging.python.org/en/latest/glossary/#term-Source-Distribution-or-sdist) for your
package, by default only a minimal set of files are included. You may find yourself wanting to include extra files in
the source distribution, such as an authors/contributors file, a `docs/` directory, or a directory of data files used
for testing purposes. There may even be extra files that you need to include; for example, if your `setup.py` computes
your project's `long_description` by reading from both a README and a changelog file, you'll need to include both those 
files in the sdist so that people who build or install from the sdist get the correct results.

**Adding & removing files to & from the source distribution is done by writing a "MANIFEST.in file" at the project
root**.

* TOC
{:toc}


How Files Are Included in a sdist
---------------------------------

The following files are included in a source distribution by default:

* all Python source files implied by the
  [py_modules](https://docs.python.org/3.7/distutils/examples.html#pure-python-distribution-by-module) and `packages` 
  `setup()` fir arguments
* all C source files mentioned in the `ext_modules` or `libraries` for `setup()` arguments
* scripts specified by the `scripts` argument for `setup()`
* all files specified by the `package_data` and `data_files` arguments for `setup()`
* the file specified by the `license_file` option in `setup.cfg` (setuptools 40.8.0+)
* all files specified by the `license_files` option in `setup.cfg` (setuptools 42.0.0+)
* all files matching the pattern `test/test*.py`
* `setup.py` (or whatever you called your setup script)
* `setup.cfg`
* `README`
* `README.txt`
* `README.rst` (Python 3.7+ or setuptools 0.6.27+)
* `README.md` (setuptools 36.4.0+)
* `pyproject.toml` (setuptools 43.0.0+)
* `MANIFEST.in`

After adding the files above to the sdist, the commands in MANIFEST.in (if such a file exists) are executed in order to
add and remove further files _to_ and _from_ the sdist. Default files can also be removed from the sdist with the 
appropriate MANIFEST.in command.

After processing the MANIFEST.in file, setuptools removes the `build/` directory as well as any directories named `RCS`, 
`CVS`, or `.svn` from the sdist, and it adds a `PKG-INFO` file and an `*.egg-info` directory. This behavior cannot be 
changed with MANIFEST.in.


MANIFEST.in Commands
--------------------

A MANIFEST.in file consists of commands, one per line, instructing setuptools to add or remove some set of files from
sdist. The commands are:

| **Command**                                   | **Description**                                                                                                         |
|-----------------------------------------------|-------------------------------------------------------------------------------------------------------------------------|
| `include pat1 pat2 ...`                       | Add all files matching any of the listed patterns (Files must be given as paths relative to the root of the project)    |
| `exclude pat1 pat2 ...`                       | Remove all files matching any of the listed patterns (Files must be given as paths relative to the root of the project) |
| `recursive-include dir-pattern pat1 pat2 ...` | Add all files under directories matching `dir-pattern` that match any of the listed patterns                            |
| `recursive-exclude dir-pattern pat1 pat2 ...` | Remove all files under directories matching `dir-pattern` that match any of the listed patterns                         |
| `global-include pat1 pat2 ...`                | Add all files anywhere in the source tree matching any of the listed patterns                                           |
| `global-exclude pat1 pat2 ...`                | Remove all files anywhere in the source tree matching any of the listed patterns                                        |
| `graft dir-pattern`                           | Add all files under directories matching `dir-pattern`                                                                  |
| `prune dir-pattern`                           | Remove all files under directories matching `dir-pattern`                                                               |

The patterns here are [glob-style patterns](https://en.wikipedia.org/wiki/Glob_(programming)): `*` matches zero or more 
regular filename characters (on Unix, everything except forward slash; on Windows, everything except backslash and
colon); `?` matches a single regular filename character, and `[chars]` matches any one of the characters between the
square brackets (which may contain character ranges, e.g., `[a-z]` or `[a-fA-F0-9]`). Setuptools also has undocumented 
support for `**` matching zero or more characters including forward slash, backslash, and colon.

Directory patterns are relative to the root of the project directory; e.g., `graft example*` will include a directory
named examples in the project root but will not include `docs/examples/`.

File & directory names in MANIFEST.in should be `/`-separated; setuptools will automatically convert the slashes to the 
local platform's appropriate directory separator.

Commands are processed in the order they appear in the MANIFEST.in file. For example, given the commands:

```manifest
graft tests
global-exclude *.py[cod]
```

the contents of the directory tree `tests` will first be added to the sdist, and then after that all files in the sdist 
with a `.pyc`, `.pyo`, or `.pyd` extension will be removed from the sdist. If the commands were in the opposite order, 
then `*.pyc` files etc. would be only be removed from what was already in the sdist before adding tests, and if tests 
happened to contain any `*.pyc` files, they would end up included in the sdist because the exclusion happened before
they were included.

### Example

So far we know MANIFEST.in is really just a list of relative file paths specifying files or globs to include.:

```manifest
include README.rst
include docs/*.txt
include funniest/data.json
```

In order for these files to be copied at install time to the package's folder inside site-packages, we need to supply 
`include_package_data=True` to the `setup()` function.

> 📋 Files which are to be used by our installed library (e.g. data files to support a particular computation method) 
> should usually be placed inside of the Python module directory itself. That way, code which loads those files can
> easily specify a relative path from the consuming
> [module's `__file__` variable](https://stackoverflow.com/a/9271479/14312712).
