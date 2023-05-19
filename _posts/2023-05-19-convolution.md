---
layout: post
title: Convolution
tags: [CNN, Mathematics]
color: rgb(255, 111, 0)
feature-img: "assets/img/post-cover/20-cover.png"
thumbnail: "assets/img/post-cover/20-cover.png"
authors: [QubitPi]
excerpt_separator: <!--more-->
---

<!--more-->

* TOC
{:toc}

In mathematics (in particular, [functional analysis](https://en.wikipedia.org/wiki/Functional_analysis)), convolution
is a mathematical operation on two functions ($$\mathit{f}$$ and $$\mathit{g}$$) that produces a third function (
$$\mathit{f \ast g}$$) that expresses how the shape of one is modified by the other. The term convolution refers to both
the result function and to the process of computing it. It is defined as the _integral of the product of the two 
functions after one is reflected about the y-axis and shifted_. The choice of which function is reflected and shifted 
before the integral does not change the integral result (see commutativity). The integral is evaluated for all values
of shift, producing the convolution function.

Definition
----------

The convolution of $$\mathit{f}$$ and $$\mathit{g}$$ is written $$\mathit{f \ast g}$$, denoting the operator with the symbol $$\ast$$ It is defined as the _integral of the product of the two functions after one is reflected about the y-axis and shifted_. As such, it is a particular kind of
[integral transform](https://en.wikipedia.org/wiki/Integral_transform):

$$\mathit{ (f \ast g)(t) :=\int_{-\infty}^{\ \infty}{f(\tau)g(t - \tau)d\tau} }$$


An equivalent definition is (see [commutativity](https://en.wikipedia.org/wiki/Convolution#Properties)):

$$\mathit{ (f \ast g)(t) :=\int_{-\infty}^{ \infty}{f(t - \tau)g(\tau)d\tau} }$$

At each $$\mathit{t}$$, the convolution formula can be described as the area under the function $$\mathit{f(\tau)}$$ 
weighted by the function $$\mathit{g(-\tau)}$$ shifted by the amount $$\mathit{t}$$. As $$\mathit{t}$$ changes, the 
weighting function $$\mathit{g(t - \tau)}$$ emphasizes different parts of the input function $$\mathit{f(\tau)}$$; If
$$\mathit{\tau}$$is a positive value, then $$\mathit{g(t - \tau)}$$ is equal to $$\mathit{g(-\tau)}$$ that slides or
is shifted along the $$\mathit{\tau}$$-axis toward the right (toward +∞) by the amount of $$\mathit{t}$$, while if
$$\mathit{t}$$is a negative value, then $$\mathit{g(t - \tau)}$$ is equal to $$\mathit{g(-\tau)}$$ that slides or is 
shifted toward the left (toward -∞) by the amount of $$\mathit{|t|}$$.

2D Convolution using Python & NumPy
-----------------------------------

2D Convolutions are instrumental when creating convolutional neural networks or just for general image processing
filters such as blurring, sharpening, edge detection, and many more. They are based on the idea of using a kernel and
iterating through an input image to create an output image.

In this section we will be implementing a 2D Convolution and then applying an edge detection kernel to an image using
the 2D Convolution.

### Dependencies

For this implementation of a 2D Convolution we will need 2 libraries:

```python
import cv2 
import numpy as np
```

OpenCV will be used to [pre-process the image](#pre-processing-image) while NumPy will be used to implement the actual 
[convolution](#2d-convolution).

### Pre-processing Image

In order to get the best results with a 2D convolution, it is generally recommended that we process the image in 
[grayscale](https://qubitpi.github.io/jersey-guide/2023/05/19/cnn.html#an-image-is-a-matrix-of-pixel-values). To do
this we can write a method with 1 parameter which will be the image file name. We will want to make sure our is stored 
in the same directory as the python file, else we may have to specify the full path. To read the contents and turn it
to grayscale, we can add the following lines of code:

```python
def processImage(image): 
  image = cv2.imread(image) 
  image = cv2.cvtColor(src=image, code=cv2.COLOR_BGR2GRAY) 
  return image
```

When reading images with OpenCV, the default mode is BGR and not RGB, so we will want to specify the code parameter as 
_BGR2GRAY_, allowing us to turn the BGR image into a grayscaled image. We will then return the new image.

### 2D Convolution

We will make so that the image and kernel are specified by the user and the default padding around the image is 0 and 
default [stride](https://qubitpi.github.io/jersey-guide/2023/05/19/cnn.html#the-convolution-step) is 1.

The next thing that we must do is to apply cross correlation to our kernel and this can be done using NumPy very easily through just flipping the matrix horizontally then vertically. This looks like:







```python
import cv2 
import numpy as np

def processImage(image): 
  image = cv2.imread(image) 
  image = cv2.cvtColor(src=image, code=cv2.COLOR_BGR2GRAY) 
  return image
  
def convolve2D(image, kernel, padding=0, strides=1):
    # Cross Correlation
    kernel = np.flipud(np.fliplr(kernel))

    # Gather Shapes of Kernel + Image + Padding
    xKernShape = kernel.shape[0]
    yKernShape = kernel.shape[1]
    xImgShape = image.shape[0]
    yImgShape = image.shape[1]

    # Shape of Output Convolution
    xOutput = int(((xImgShape - xKernShape + 2 * padding) / strides) + 1)
    yOutput = int(((yImgShape - yKernShape + 2 * padding) / strides) + 1)
    output = np.zeros((xOutput, yOutput))

    # Apply Equal Padding to All Sides
    if padding != 0:
        imagePadded = np.zeros((image.shape[0] + padding*2, image.shape[1] + padding*2))
        imagePadded[int(padding):int(-1 * padding), int(padding):int(-1 * padding)] = image
        print(imagePadded)
    else:
        imagePadded = image

    # Iterate through image
    for y in range(image.shape[1]):
        # Exit Convolution
        if y > image.shape[1] - yKernShape:
            break
        # Only Convolve if y has gone down by the specified Strides
        if y % strides == 0:
            for x in range(image.shape[0]):
                # Go to next row once kernel is out of bounds
                if x > image.shape[0] - xKernShape:
                    break
                try:
                    # Only Convolve if x has moved by the specified Strides
                    if x % strides == 0:
                        output[x, y] = (kernel * imagePadded[x: x + xKernShape, y: y + yKernShape]).sum()
                except:
                    break

    return output
    
if __name__ == '__main__':
    # Grayscale Image
    image = processImage('input.png')

    # Edge Detection Kernel
    kernel = np.array([[-1, -1, -1], [-1, 8, -1], [-1, -1, -1]])

    # Convolve and Save Output
    output = convolve2D(image, kernel, padding=2)
    cv2.imwrite('output.png', output)
```

Reference
---------

- [Wikipedia: Convolution](https://en.wikipedia.org/wiki/Convolution#Domain_of_definition)
- [2D Convolution using Python & NumPy](https://medium.com/analytics-vidhya/2d-convolution-using-python-numpy-43442ff5f381)
