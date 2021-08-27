# jOOQ Gradle Example with Testcontainers and Flyway

## Introduction

This example project shows how someone can use a SQL schema to 

## Scope of the Project

What is supported:
* 
What needs to be fixed:

* The database project has some unnecessary jOOQ dependency that leak into the application
* The generator doesn't log. It's hard to trace an error in this code (Gradle build failed --> Where and why?!)
* 