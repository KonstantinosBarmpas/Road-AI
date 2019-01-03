# **RoadAI**

## Side Project for PyTorch Facebook - Udacity Challenge Scholarship.

---

[rpy1]: ./images/Results1.png
[rpy2]: ./images/Results2.png
[rpy3]: ./images/Results3.png
[app]: ./images/App.png
[logo]: ./images/Logo.png

[t1]: ./images/result00001.png
[t2]: ./images/result00002.png

[ios1]: ./images/resultios00001.png
[ios2]: ./images/resultios00002.png
[ios3]: ./images/resultios00003.png
[ios4]: ./images/resultios00004.png

**Introduction**

![logo]

This is a side project for PyTorch Facebook - Udacity Challenge Scholarship. In this scholarship, 10.000 accepted students will spend 2 months building powerful deep learning models with PyTorch. Top 300 students from the initial challenge course will be selected for the Deep Learning Nanodegree program.

The project is called RoadAI, mobile app and deep learning model. 
Goal: Building the most intelligent co-driver.

* The project is NOT finished. Expected release date: August 2019

**About**

RoadAI is a mobile app that brings the most intelligent co-driver to your pocket. 

How does RoadAI work ?

![app]

Place your phone in car phone holder and press "Begin my trip button". Your phone detects red traffic lights, stop signs, cars and pedestrians, warns you and makes your driving experience safer. 

Users can also share their ride footage and contribute in making the deep learning model even smarter.

**Motivation**

Over the next 10 years, more and more self-driving cars will hit our roads. It is going to be a revolution in transportation. But according to studies, only 21% of people trust self-driving cars. RoadAI ( by showing them how their phone and consequently a self-driving car understands the world around it using deep learning and computer vision ) intends to make users more positive into trusting their transportation to self-driving cars. 

In addition to that, RoadAI can reduce accidents by warning users for red traffic lights, pedestrians, cars or stop signs that they might have missed.

**Project Structure - Files**

RoadAI has been implemented using both PyTorch and Tensorflow Object Detection. This repo contains the PyTorch classifier implementation and the Tensorflow .pb trained model. 

SOON there will be the Android app and iOS app folders. As well as I will keep releasing the newest trained models here.

**PyTorch Model**

VGG-19 is an excellent Convolutional Neural Network (CNN) for image classification and I used the pre-trained version that exists in PyTorch models package. After freezing the main features of the network to avoid changes during backpropagation, I replaced the classifier part with my own designed classifier. The classifier is inspired by NVIDIA's neural network used again in Behavioural Cloning Project (check repo) with an added layer of 20588 neurons in the beginning and of course change the number of classes for now to 4 (Cars, Green, Red and Yellow).


**PyTorch Training**

During training, I used the pocket algorithm's idea to store and save the model with the minimum validation error to avoid overfitting during unneccesary training steps. The training was done in my local CPU.

**Pytorch Results**

![rpy1]
![rpy2]
![rpy3]

The model had more than >80% accuracy in the validation test.

**Tensorflow Object Detection Model**

Choice of the pre-trained model and the model's technique:

For our models, I used the Tensorflow object detection API as described in the tutorial below:

https://pythonprogramming.net/introduction-use-tensorflow-object-detection-api-tutorial/

After seaching on the web, I've decided that a good compromise of accuracy and execution time is the SSD_mobilnet and for that reason I chose the ssd_mobilenet_v1_coco_2017_11_17 pre-trained model.

**Tensorflow Object Detection Training -- Instructions**

To get started clone the Tensorflow API repo:

https://github.com/tensorflow/models/tree/master/research/object_detection

--------------Datasets------------------:

To train the model, I used the train dataset and to test it the test dataset. More accurately, I used the TFRecord files on each data set as described in the tutorial.

-----------------Training---------------:

Set up:

1) Go to models - > research -> object_detection ->legacy
2) In the data folder put the train.record and test.record files you want to use
3) In the training folder configure the pipeline (basically the number of steps and learning rate)

Train:

1) Open terminal
2) Navigate to models -> research (cd models -> cd research)
3) Execute the following:
```
protoc object_detection/protos/*.proto --python_out=.
export PYTHONPATH=$PYTHONPATH:`pwd`:`pwd`/slim
```
4) Go to object_detection folder (cd object_detection) and the to legacy (cd legacy)
5) Execute the folllowing:

```
python3 train.py --logtostderr --train_dir=training/ --pipeline_config_path=training/pipeline.config
```

After the model is trained:

1) Move the three last saved files from the training folder in legacy to the training file in training folder in object_detection.
2) Open terminal
3) Navigate to models - > research -> object_detection
4) Execute to save the graph:

```
python3 export_inference_graph.py \
--input_type image_tensor \
--pipeline_config_path training/pipeline.config \
--trained_checkpoint_prefix training/YOUR-LAST-SAVE-MODEL(eg model.ckpt-20000)\
--output_directory A-NEW-NAME-DIRECTORY

```

--------------Test the model------------------:

1) Open the Jupyter notebook in the object_detection folder
2) Change the models name
3) Run all cells

--------------Important notes------------------------:

1) The models were trained in Amazon AWS g3.4 large instance.
Visit:

https://aws.amazon.com

2) You may find it useful to use FileZilla to transfer your data in the instance.
Visit:

https://filezilla-project.org


**Tensorflow Results**

![t1]
![t2]

**Results using the iOS app running on a IphoneX**

![ios1]
![ios2]
![ios3]
![ios4]

---
