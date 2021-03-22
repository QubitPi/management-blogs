---
layout: post
title: ImageMagick Reference
tags: [ImageMagick]
color: rgb(250, 154, 133)
feature-img: "assets/img/pexels/design-art/2020-08-16-25-jersey-cdi-container-agnostic-support/cover.png"
thumbnail: "assets/img/pexels/design-art/2020-08-16-25-jersey-cdi-container-agnostic-support/cover.png"
author: QubitPi
excerpt_separator: <!--more-->
---

<!--more-->

* TOC
{:toc}

## [Generate Favicon File](https://unix.stackexchange.com/a/89276)

```bash
convert image.png  -bordercolor white -border 0 \
      \( -clone 0 -resize 16x16 \) \
      \( -clone 0 -resize 32x32 \) \
      \( -clone 0 -resize 48x48 \) \
      \( -clone 0 -resize 64x64 \) \
      -delete 0 -alpha off -colors 256 favicon.ico
```

## Replace Transparent Background in png with White Background

    convert -flatten img1.png img1-white.png
    
## Concatenate Images Vertically

    convert -append in-*.jpg -set colorspace RGB out.jpg
    
## Concatenate Images Horizontally

    convert +append in-*.jpg -set colorspace RGB out.jpg
    
## Reduce Image Size

    convert image.png -resize 50% image.png

## Resize Image

    convert input.png -resize 240x192 output.png
    
By default the aspect ratio of the image is preserved. This is done by reducing either the width or the height of the
output by an appropriate amount. If you do not want this to happen, and are prepared to accept the resulting distortion,
then you can allow the aspect ratio to change by appending an exclamation mark to the required size. Since this is a
shell metacharacter it must be escaped:

    convert input.png -resize 240x192\! output.png
    
## Save .gif from Web

If a `.gif` file can only be saved in static(e.g. jpg) format from website, just download it and convert format using

    convert x.jpg x.gif
    
## Decompose `.gif`

    convert x.gif y.png
    
## Generate Blank (White) Image

    convert -size 57x57 xc:white 57.png
    
## Add White Padding so that the Original Image is Positioned in North-West Corner

    convert -size 400x400 xc:white input.png -gravity northwest -composite output.png
    
## Convert Multiple Images to PDF

    convert 1.jpg 2.jpg output.pdf
    
### Define the File Order for ImageMagick Convert
 
Suppose you have 100 png's whose names are 1.png, 2.png, ..., 100.png. To make sure the PDF shows up with the same page
order, use
   
        convert $(for i in $(seq 1 1 100); do echo "page ${i}.png"; done) out.pdf
    
## Batch Processing

### Convert all `jpg` file to `png` with names starting with 1.png, 2.png, etc

#### Using ImageMagic

    convert *.jpg -scene 1 %d.png
   
