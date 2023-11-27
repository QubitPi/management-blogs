[![License Badge](https://img.shields.io/badge/License-Apache%202.0-orange.svg?style=for-the-badge) ](https://www.apache.org/licenses/LICENSE-2.0)

[A Complete Jersey Guide Book](https://qubitpi.github.io/management-blogs/)
------------------------------

### The origin of name "Jersey Guide"

Jersey has been an industry standard for standing up Webservices; I love it and opened up a blog site to put my study
notes for it. So [Jersey Guide](https://qubitpi.github.io/management-blogs/) was born. Later on, it has been used
extensively as a hub assembling all of my tech-related study notes. The jersey

### Add-on

1. Add the **authors**, which is a list of author ID's, into the post metadata section:

   ```markdown
   ---
   layout: post
   title: Using OpenSSL to encrypt messages and files on Linux
   tags: [Security, OpenSSL]
   color: rgb(43, 164, 78)
   feature-img: "assets/img/post-cover/32-cover.png"
   thumbnail: "assets/img/post-cover/32-cover.png"
   authors: [linuxconfig.org, QubitPi]
   excerpt_separator: <!--more-->
   ---
   ```
   
   The author ID's and each author's info is defined if file [authors.yml](./docs/_data/authors.yml)

2. In [**post.html**](./docs/_layouts/post.html), pass the author's ID into `post_info.html`. This can be done by
   changing the following line

   `{% include post_info.html author=page.author date=page.date %}`

   to

   ```html
   {% capture author_list %}{{ page.authors | join: "|"}}{% endcapture %}
   {% include post_info.html authors=author_list date=page.date %}
   ```

3. In [**post_info.html**](./docs/_includes/post_info.html), iterate the author's ID, map-retrieve the author info from
   config file (`docs/_data/authors.yml`) and load their info onto the page:

   ```html
   {% assign date = include.date %}
   {% assign authors = include.authors | split:'|' | sort | uniq %}
  
   <div class="post-info">
     {% for author_id in authors %}
       {% assign author = site.data.authors[author_id] %}
  
       {%- if author.url -%}<a href="{{ author.url | relative_url }}" target="_blank">{%- endif -%}
       {% if author.avatar  %}
       <img src="{{ author.avatar | relative_url }}">
       {% endif %}
       <p class="meta">
           {% if author.name %}{{ author.name }} {% endif %}
       </p>
       {%- if author.url -%}</a>{%- endif -%}
     {% endfor %}
  
     <br>
  
     {{ date | date: "%B %-d, %Y" }}
   </div>
   ```


License
-------

The use and distribution terms for this software are covered by the Apache License, Version 2.0
( http://www.apache.org/licenses/LICENSE-2.0.html ).
