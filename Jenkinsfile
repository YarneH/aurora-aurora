#!/usr/bin/env groovy
import net.sf.json.JSONArray
import net.sf.json.JSONObject

pipeline {
    agent any

    environment {
        scannerHome = tool 'sonarscanner'
    }

    stages {
        stage('Compile') {

            steps {
                // Compile the app and its dependencies
                  sh './gradlew clean compileDebugSources'
            }

            post {
                failure {
                    slack_error_build()
                }
            }
        } // Compile stage

        stage('Unit test') {
            steps {
                script {
                    // Compile and run the unit tests for the app and its dependencies
                    if (env.BRANCH_NAME == 'master' || env.BRANCH_NAME == 'dev') {
                        sh './gradlew testReleaseUnitTest'
                    } else {
                        sh './gradlew testDebugUnitTest'
                    }


                    // Analyse the test results and update the build result as appropriate
                    junit allowEmptyResults: true, testResults: '**/TEST-*.xml'

                    // Analyze coverage info
                    jacoco sourcePattern: '**/src/*/java', 
                        classPattern: '**/classes/com/aurora', 
                        exclusionPattern: '**/*Test*.class,  **/aurora/*.class, **/src/test, **/src/androidTest'
                }
            }

            post {
                failure {
                    slack_error_test()
                }
            }
        } // Unit test stage

        stage('SonarQube') {
            steps {
                withSonarQubeEnv("Aurora SonarQube") {
                    sh "${scannerHome}/bin/sonar-scanner -X -Dproject.settings=sonar-project.properties -Dsonar.branch=${env.BRANCH_NAME}"
                }
                script {
                    timeout(time: 1, unit: 'HOURS') {
                        def qg = waitForQualityGate()
                        if (qg.status != 'OK') {
                            error 'Sonarqube failed.'
                        }
                    }
                }
            }

            post {
                failure {
                    slack_error_sonar()
                }
            success {
                slack_success()
            }
            }
        } // SonarQube stage

        stage('Javadoc') {
            when {
                anyOf {
                    branch 'master';
                    branch 'dev';
                    branch 'javadoc-pipeline'
                }
            }
            steps {
                script {
                    def classPathName = ""

                    if (env.BRANCH_NAME == 'master' || env.BRANCH_NAME == 'dev') {
                        classPathName = "release/compileReleaseJavaWithJavac"
                    } else {
                        classPathName = "debug/compileDebugJavaWithJavac"
                    }

                    // Generate javadoc
                    sh """
                    javadoc -d /var/www/javadoc/aurora/${env.BRANCH_NAME} -sourcepath ${WORKSPACE}/app/src/main/java -subpackages com -private \
                    -classpath ${WORKSPACE}/app/build/intermediates/javac/${classPathName}/classes
                    """
                }
            }
            post {
                failure {
                    slack_error_doc()
                }
                success {
                    slack_success_doc()
                }
            }
        } // Javadoc stage
    } // Stages
} // Pipeline


/**
 * Gets called when build of the project fails
 */
def slack_error_build() {
    slack_report(false, ':x: Aurora could not be built.', null, 'Build')
}


/**
 * Gets called when tests fail
 */
def slack_error_test() {
    slack_report(false, ':x: Tests failed', null, 'Unit Test')
}

/**
 * Gets called when sonar fails
 */
def slack_error_sonar() {
    slack_report(false, ':x: Sonar failed', null, 'SonarQube analysis')
}

/**
 * Gets called when javadoc fails
 */
def slack_error_doc() {
    slack_report(false, ':x: Javadoc failed', null, 'Javadoc')
}


/**
 * Gets called when build succeeds
 */
def slack_success() {
    slack_report(true, ':heavy_check_mark: Build succeeded', null, '')
}


/**
 * Gets called when the javadoc is successfully generated
 */
def slack_success_doc() {
    slack_report(true, ':heavy_check_mark: Javadoc generated', null, '')
}

/**
 * Find name of author
 * @param  email the commit author's email
 * @return       A string containing the author's name
 */
static String author_name(String email) {
    if (email.startsWith("robbe")) {
        return "Robbe"
    } else if (email.startsWith("jeroen")) {
        return "Jeroen"
    } else if (email.startsWith("jonas.ta")) {
        return "Tack"
    } else if (email.startsWith("jonas.cu")) {
        return "Cuypers"
    } else if (email.startsWith("smidpauw")) {
        return "Stijn"
    } else if (email.startsWith("piet")) {
        return "Piet"
    } else if (email.startsWith("yarne")) {
        return "Yarne"
    } else if (email.startsWith("luca")) {
        return "Luca"
    }

    // Fallback
    return email
}


/**
 * Report results of pipeline to slack
 * @param  successful     was build successful
 * @param  text           Message to display
 * @param  fields         extra fields to include in the message
 * @param  failedStage='' If build failed, at which stage did it fail
 * @return                void
 */
def slack_report(boolean successful, String text, JSONArray fields, failedStage='') {
    JSONArray attachments = new JSONArray()
    JSONObject attachment = new JSONObject()

    // Get email of commit author
    String author_email = sh(script: 'git show -s --pretty=%ae | head -n1', returnStdout: true).trim()

    // Get commit info
    String commit = env.GIT_COMMIT
    String commit_branch = env.BRANCH_NAME
    String commit_hash = "#" + commit.substring(0, 8)
    String commit_message = sh(script: 'git show -s --pretty=%B | head -n1', returnStdout: true).trim()

    // Configure author information
    attachment.put('author_name', author_name(author_email))

    // Configure message details
    attachment.put('color', successful ? 'good' : 'danger')
    attachment.put('title', commit_message + ' @ branch ' + env.BRANCH_NAME)
    attachment.put('title_link', 'https://github.ugent.be/Aurora/aurora/commit/' + commit)
    if (text != '') {
        attachment.put('text', text)
    }

    JSONArray actions = new JSONArray()

    // Add actions to message
    JSONObject actionViewBuild = new JSONObject()

    // Add a button 'build log' to message that links to Jenkins
    actionViewBuild.put('type', 'button')
    actionViewBuild.put('text', 'Build log')
    actionViewBuild.put('url', env.BUILD_URL)

    // Add if build succeeded or failed
    if (successful) {
        attachment.put('fallback', 'Run succeeded @ ' + commit_branch + '.')

        actionViewBuild.put('style', 'primary')
    } else {
        attachment.put('fallback', failedStage + ' failed @ ' + commit_branch + '.')

        actionViewBuild.put('style', 'danger')
    }

    actions.add(actionViewBuild)

    // Add a button 'sonar log' to message that links to sonar (if sonar failed or if build was successful)
    if (successful || failedStage == 'SonarQube analysis') {
        JSONObject actionViewSonar = new JSONObject()
        actionViewSonar.put('type', 'button')
        actionViewSonar.put('text', 'Sonar log')
        actionViewSonar.put('url', 'http://sonarqube.aurora-files.ml/projects')

        if (successful) {
            actionViewSonar.put('style', 'primary')
        } else {
            actionViewSonar.put('style', 'danger')
        }

        actions.add(actionViewSonar)
    }

    attachment.put('actions', actions)

    // Add fields to message
    if (fields != null) {
        attachment.put('fields', fields)
    }

    attachment.put('footer', commit)
    // Add commit to message

    // Add github logo to message
    attachment.put('footer_icon', 'https://github.githubassets.com/images/modules/logos_page/Octocat.png')

    attachments.add(attachment)

    String token = successful ? 'TD60N85K8/BG960T35H/zH59dbicld2uw5Tfdaipg0oL' : 'TD60N85K8/BGABQ0CS3/xS539cEwbxr6cMPvk7LMu7Ve'

    slackSend(channel: '#aurora-builds', attachments: attachments.toString(), teamDomain: 'aurora1819', baseUrl: 'https://hooks.slack.com/services/', token: token)
}


def slack_deployed() {
    JSONArray attachments = new JSONArray()
    JSONObject attachment = new JSONObject()

    // Get commit info
    String commit = env.GIT_COMMIT
    String commit_branch = env.BRANCH_NAME
    String commit_hash = "#" + commit.substring(0, 8)
    String commit_message = sh(script: 'git show -s --pretty=%B | head -n1', returnStdout: true).trim()

    attachment.put('color', 'good')
    attachment.put('title', commit_message + ' @ branch ' + env.BRANCH_NAME)
    attachment.put('title_link', 'https://github.ugent.be/Aurora/aurora/commit/' + commit)
    attachment.put('text', ':heavy_check_mark: New version deployed!')

    JSONArray actions = new JSONArray()

    // Add actions to message
    JSONObject actionViewBuild = new JSONObject()

    // Add a button 'build log' to message that links to Jenkins
    actionViewBuild.put('type', 'button')
    actionViewBuild.put('text', 'Build log')
    actionViewBuild.put('url', env.BUILD_URL)

    actions.add(actionViewBuild)

    attachment.put('actions', actions)

    attachment.put('footer', commit)
    // Add commit to message

    // Add github logo to message
    attachment.put('footer_icon', 'https://github.githubassets.com/images/modules/logos_page/Octocat.png')

    attachments.add(attachment)

    String token = 'TD60N85K8/BG960T35H/zH59dbicld2uw5Tfdaipg0oL'

    slackSend(channel: '#aurora-builds', attachments: attachments.toString(), teamDomain: 'aurora1819', baseUrl: 'https://hooks.slack.com/services/', token: token)
}
