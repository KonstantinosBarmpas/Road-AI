# **RoadAI**

## Building the Most Intelligent Co-Driver

[![License: MIT](https://img.shields.io/badge/License-MIT-yellow.svg)](https://opensource.org/licenses/MIT)

---

[logo]: ./images/roadai_logo.png

[app1]: ./images/roadai1.png
[app2]: ./images/roadai2.png
[app3]: ./images/roadai3.png
[app4]: ./images/roadai4.png


**Introduction**

![logo]

Over the next 10 years, more and more self-driving cars will hit our roads. It is going to be a revolution in transportation. But according to recent studies, only 21% of people fully trust self-driving cars. With RoadAI Android app we decided to fill that gap. Using Artificial Intelligence, RoadAI intends to make people trust their transportation to self-driving cars by showing them how their phone and consequently a self-driving car perceives the world around it, acting as an intelligent co-driver. Simple setup a phone holder for cars and your phone. Self-driving cars promise safer roads for everyone and we decided to demonstrate that as well. In every driver’s life, there have been incidents where they have missed a red traffic light or stop sign. RoadAI detects and vocally warns the driver of potential dangers making the driving experience much safer.

**PyTorch Model**

Using PyTorch framework from Facebook and public datasets, we trained an AI model to detect Red Traffic lights, cars, pedestrians and stop signs. Then we converted the model to fit into an android device and we built the app around it. The model had more than >90% accuracy in the validation test.

**Android App**

![app1] ![app4] ![app2] ![app3] 

Your phone detects red traffic lights, stop signs, cars and pedestrians, warns you and makes your driving experience safer.  Users can also share their ride footage and contribute in making the model even smarter and more robust.

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

**Project Structure - Files**

- PyTorch Model
- PyTorch Training
- Tensorflow Model
- PyTorch to Android Convertion Model
- Android Apps 

---
