---
layout: post
title: GitHub Artifactory with Apache Maven Registry
tags: [CI, Maven, Software Release]
color: rgb(11, 164, 105)
feature-img: "assets/img/post-cover/34-cover.png"
thumbnail: "assets/img/post-cover/34-cover.png"
author: QubitPi
excerpt_separator: <!--more-->
---

We can configure Apache Maven to publish packages to GitHub Packages and to use packages stored on GitHub Packages as 
dependencies in a Java project.

<!--more-->

* TOC
{:toc}

Authenticating to GitHub Packages
---------------------------------

You need an access token to publish, install, and delete packages.

You can use a **personal access token (PAT)** to authenticate to GitHub Packages or the GitHub API. When you create a
personal access token, you can assign the token different scopes depending on your needs.

### Creating a Personal Access Token

> You can create a personal access token to use in place of a password with the command line or with the API.

1. [Verify your email address](https://docs.github.com/en/github/getting-started-with-github/verifying-your-email-address), if it hasn't been verified yet.

2. In the upper-right corner of any page, click your profile photo, then click **Settings**.

   ![Error loading userbar-account-settings.png]({{ "/assets/img/userbar-account-settings.png" | relative_url}})

3. In the left sidebar, click **Developer settings**.

4. In the left sidebar, click **Personal access tokens**.

   ![Error loading personal_access_tokens_tab.png]({{ "/assets/img/personal_access_tokens_tab.png" | relative_url}})

5. Click **Generate new token**.

   ![Error loading generate_new_token.png]({{ "/assets/img/generate_new_token.png" | relative_url}})

6. Give your token a descriptive name.


   ![Error loading token_description.png]({{ "/assets/img/token_description.png" | relative_url}})

7. To give your token an expiration, select the **Expiration** drop-down menu, then click a default or use the calendar 
   picker.

   ![Error loading token_expiration.png]({{ "/assets/img/token_expiration.png" | relative_url}})

8. Select the scopes, or permissions, you'd like to grant this token. To use your token to publish Maven package, check
   the "**write:packages**" option

9. Click **Generate token**.

   ![Error loading generate_token.png]({{ "/assets/img/generate_token.png" | relative_url}})

   ![Error loading personal_access_tokens.png]({{ "/assets/img/personal_access_tokens.png" | relative_url}})


> ⚠️ Treat your tokens like passwords and keep them secret. When working with the API, use tokens as environment
> variables instead of hardcoding them into your programs.

### Authenticating to GitHub Packages with Apache Maven

To authenticate to GitHub Packages with Apache Maven, edit the `~/.m2/settings.xml` file to include your personal access 
token. Create a new `~/.m2/settings.xml` file if one doesn't exist and populate the file with the following template:

```xml
<settings xmlns="http://maven.apache.org/SETTINGS/1.0.0"
  xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
  xsi:schemaLocation="http://maven.apache.org/SETTINGS/1.0.0
                      http://maven.apache.org/xsd/settings-1.0.0.xsd">

    <activeProfiles>
        <activeProfile>github</activeProfile>
    </activeProfiles>

    <profiles>
        <profile>
            <id>github</id>
            <repositories>
               <repository>
                  <id>central</id>
                  <url>https://repo1.maven.org/maven2</url>
               </repository>
               <repository>
                  <id>github</id>
                  <url>https://maven.pkg.github.com/OWNER/REPOSITORY</url>
                  <snapshots>
                     <enabled>true</enabled>
                  </snapshots>
               </repository>
            </repositories>
        </profile>
    </profiles>

     <servers>
        <server>
           <id>github</id>
           <username>USERNAME</username>
           <password>TOKEN</password>
        </server>
     </servers>
</settings>
```

> ⚠️ _The server id must be **github** otherwise we will receive 401 Unauthorized error_

In the `servers` tag, add a child `server` tag with an `id`, replacing _USERNAME_ with your GitHub username, and _TOKEN_ 
with your personal access token.

In the `repositories` tag, configure a repository by mapping the `id` of the repository to the `id` you added in the 
`server` tag containing your credentials. Replace _OWNER_ with the name of the user or organization account that owns
the repository. Because uppercase letters aren't supported, you must use lowercase letters for the repository owner even 
if the GitHub user or organization name contains uppercase letters.

GitHub Packages supports `SNAPSHOT` versions of Apache Maven. To use the GitHub Packages repository for downloading `SNAPSHOT` artifacts, enable `SNAPSHOTS` in the POM of the consuming project or the `~/.m2/settings.xml` file.

Publishing the Package
----------------------

By default, GitHub publishes the package to an existing repository with the same name as the package. For example, GitHub 
will publish a package named `com.example:test` in a repository called `OWNER/test`.

If you would like to publish multiple packages to the same repository, you can include the URL of the repository in the 
`<distributionManagement>` element of the `pom.xml` file. GitHub will match the repository based on that field. Since
the repository name is also part of the `distributionManagement` element, there are no additional steps to publish 
multiple packages to the same repository.

For more information on creating a package, see the
[maven.apache.org documentation](https://maven.apache.org/guides/getting-started/maven-in-five-minutes.html).

Edit the `distributionManagement` element of the `pom.xml` file located in your package directory, replacing `OWNER` 
with the name of the user or organization account that owns the repository and `REPOSITORY` with the name of the  
repository containing your project.

```xml
<distributionManagement>
   <repository>
      <id>github</id>
      <name>GitHub OWNER Apache Maven Packages</name>
      <url>https://maven.pkg.github.com/OWNER/REPOSITORY</url>
   </repository>
</distributionManagement>
```

At this moment, we could manually deploy the package with `mvn deploy`. We will, however, make it a CI process using
GitHub Action. Create a `.github/workflows/ci.yml` file in package root directory like the following:

```xml
name: Publish package to GitHub Packages

on:
  push:
    branches:
      - master

jobs:
  publish:
    runs-on: ubuntu-latest 
    permissions: 
      contents: read
      packages: write 
    steps:
      - uses: actions/checkout@v3
      - uses: actions/setup-java@v3
        with:
          java-version: '8'
          distribution: 'adopt'
      - name: Publish package
        run: mvn --batch-mode deploy
        env:
          {% raw %}GITHUB_TOKEN: ${{ secrets.GITHUB_TOKEN }}{% endraw %}
```

This workflow performs the following steps:

1. Checks out a copy of project's repository.
2. Sets up the Java JDK, and also automatically configures the Maven _settings.xml_ file to add authentication for the 
   github Maven repository to use the `GITHUB_TOKEN` environment variable.
3. Runs the `mvn --batch-mode deploy` command to publish to GitHub Packages. The `GITHUB_TOKEN` environment variable
   will be set with the contents of the `GITHUB_TOKEN` secret. The permissions key specifies the access granted to the 
   `GITHUB_TOKEN`.

> For more information about using secrets in your workflow, see
> "[Creating and using encrypted secrets](https://docs.github.com/en/actions/automating-your-workflow-with-github-actions/creating-and-using-encrypted-secrets)".

Viewing the Package
-------------------

After you publish a package, you can view the package on GitHub located in a particular repository.

1. On GitHub.com, navigate to the main page of the repository.
2. To the right of the list of files, click **Packages**.

   ![Error loading packages-link.png]({{ "/assets/img/packages-link.png" | relative_url}})
   
3. Click the name of the package that you want to view.

   ![Error loading package-name.png]({{ "/assets/img/package-name.png" | relative_url}})
