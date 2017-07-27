# K8 Console

K8 Console can be thought of as a *visual command line app* for managing Kubernetes 
clusters and applications. The idea is to have a tool that combines the best of both worlds: 
The efficiency/speed of command line tools and the visual presentation capabilities of web browsers.

This started out as a side project, so there are things that need improvement:
* Test coverage (especially frontend)
* Feature completeness (see below)
* Frontend design / UX
Looking for contributors!


## Installation

WIP

## Features

### Available Commands

Status | Feature | Command
--- | --- | ---
**done** | List resources in all namespaces | `get {resource}s`
**done** | List resources in a namespace | `get {resource}s in {namespace}`
**done** | Get details for a single resource | `from {namespace} get {resource} {name}`
**done** | Run command in pod | `run "{command}" in {namespace}/{pod}`
**done** | Show logs for pod | `logs {namespace}/{pod}`
**done** | Scale deployment | `scale {namespace}/{deployment} {replicas}`
*pending* | Delete resource | `from {namespace} delete {resource}`


### Other notable features
 * All commands support auto completion using `tab`
 * Some commands support the `| watch` filter which will continue updating a command's result

### Ideas for New Features

 * A `set context {namespace}` command that executes all subsequent commands in a particular namespace
 * Separate out all `| watch` commands into an extra UI panel

## Development

The project uses [gulp](https://gulpjs.com/) for organizing development workflows.
The backend is written in Java 8 and uses the magnificent [Dropwizard](http://www.dropwizard.io/) framework.
The frontend is written in JavaScript (ES6) and uses [BackboneJS](http://backbonejs.org/) and [Materialize](http://materializecss.com/). 

### Prerequisites
 * Java 8
 * NodeJS
 
### Getting Started

1. `npm install`
2. Open proxy tunnel to your Kubernetes cluster: `kubectl proxy --port=8080`
3. Start application with `grunt serve`
4. Open [http://localhost:3000](http://localhost:3000)
