#!/usr/bin/env groovy

pipeline {
	agent any

  	stages {
    	stage('Compile') {

			steps {
				// Compile the app and its dependencies
	      		sh './gradlew clean compileDebugSources'
			}
    	}

		stage('Unit test') {
	 		steps {
	   			// Compile and run the unit tests for the app and its dependencies
	   			sh './gradlew testDebugUnitTest testDebugUnitTest'

	   			// Analyse the test results and update the build result as appropriate
	   			junit '**/TEST-*.xml'
	 		}
   		}

		stage('Static analysis') {
      		steps {
        		// Run Lint and analyse the results
		        sh './gradlew lintDebug'
		        androidLint pattern: '**/lint-results-*.xml', failedTotalAll: '0'
		    }
    	}
  	}
}
