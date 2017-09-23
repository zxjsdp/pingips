# pingips

Simple GUI (JavaFX) tool to ping multiple IPs.

## Usage

Generate jar:

    $ gradle jar
        
Run jar:

    $ java -jar build/libs/pingips.jar

## macOS application

A macOS `.app` application can be easily created with [gradle-macappbundle](https://github.com/crotwell/gradle-macappbundle).

    $ gradle createApp
    
or 

    $ gradle createDmg
