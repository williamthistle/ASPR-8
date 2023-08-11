[![GPL LICENSE][license-shield]][license-url]
[![GitHub tag (with filter)][tag-shield]][tag-url]
[![GitHub contributors][contributors-shield]][contributors-url]


# General Computational Model
This repository contains the source code for the General Computational Model herein reffered to as GCM, along with a set of tutorials that have been created to aide new users with using this model.

## What is GCM?
GCM is a Java based simulation framework for building disease progression models.  
Users of GCM should have a general familiarity with Java and object oriented programming and would benefit from some exposure to event based modeling.

## Overview
THere are 3 core tenants to GCM.

### Simulation
GCM is an event based simulation composed of data managers, actors and an event engine.  
The data managers contain the state of the simulation and generate events when that state changes.  
The actors contain the business logic of your model and act on the data managers.  
The engine transports events generated by the data managers to any data managers and actors that subscribe to those events.

### Plugins
Data managers and actors are organized into plugins. A GCM model is thus composed of the core simulation and a suite of plugins.  
The plugin architecture provides for the scalable reuse of concepts and capabilities between models.  
GCM is provided with a set of existing plugins that define many of the concepts useful to a broad range of models such as the management of people, their properties, social group structures and the like. 

The modeler is free to compose a model from their choice of plugins.

### Experiment
GCM also provides an experiment management system.  
Each plugin contains zero to many data objects that define the initial state of its actors and data managers. Each such data object may be altered freely.  
The complete set of all combinations (scenarios) of the variant plugin data objects form an experiment and a separate simulation instance is executed for each combination.

## Requirements
- Maven 3.8.x
- Java 17
- Your favroite IDE for developing Java projects
- Modeling Utilities located [here](https://github.com/HHS/ASPR-ms-util)

## Building
Once you have cloned the repo and imported it into your favorite IDE, navigate into the gcm directory on the command line.  
Once there, run the following command: ```mvn clean install```  
That's all there is to building the project.  
After running the above command, the next place you should start looking is at the Modeling Guide located in [doc](doc) and following the lessons.


## License
Distributed under the GPLv3 License. See [LICENSE](LICENSE) for more information.


<!-- MARKDOWN LINKS & IMAGES -->
[contributors-shield]: https://img.shields.io/github/contributors/HHS/ASPR-8
[contributors-url]: https://github.com/HHS/ASPR-8/graphs/contributors
[tag-shield]: https://img.shields.io/github/v/tag/HHS/ASPR-8
[tag-url]: https://github.com/HHS/ASPR-8/releases/tag/v4.0.0-RC1
[license-shield]: https://img.shields.io/github/license/HHS/ASPR-8
[license-url]: LICENSE
