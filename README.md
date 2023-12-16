# Parameter Store Tool

This is a Spring Shell tool that allows managing AWS Parameter Store with simple Posix-like commands.

It is currently rudimentary; this was written specifically to facilitate a mass-migration of parameters from one 
path layout to another using a "copy, deploy updated application, delete" flow in batches. For this, we scripted
the batches for review and pasted the scripts into the tool.

## Commands

* cat <name>
* cp <source> <destination>
* ls <glob>
* mv <source> <destination>
* rm <name>

## Setup

Setup is currently external to the tool. Use AWS environment variables such as AWS_PROFILE and AWS_REGION.

## Run

It's thus far mostly been used straight from Intellij while iterating on development. See notes below for caveats.

```shell
AWS_PROFILE=myprofile ./mvnw spring-boot:run
```

## Notes

This project suffers from well-known problems with Intellij's terminal and jline. Most notably, normal
input controls don't work, and tab completion is broken: https://youtrack.jetbrains.com/issue/IDEA-183619

`rm` supports -f / --force, but Spring Shell doesn't seem to be able to handle a boolean flag without args combined with 
standalone args, so one has to specify `rm -f true <param>`.   


## TODO
* allow cp/mv to overwrite with confirmation
* cd
* create
* ed
* tab completion for parameters
* caching (only practical way to do tab completion)
* release artifact
* backup / restore
