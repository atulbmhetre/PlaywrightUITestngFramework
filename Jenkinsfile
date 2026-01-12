pipeline {
    agent any
    tools {
        // Match the exact names you gave in Jenkins Global Tool Configuration
        nodejs 'Node25' 
        maven 'M3' 
    }
    stages {
        stage('Compile and Install') {
            steps {
                // Java projects use Maven to install dependencies, not npm
                bat 'mvn clean install -DskipTests'
            }
        }
        stage('Run Playwright Tests') {
            steps {
                // This command runs your TestNG tests
                bat 'mvn test'
            }
        }
    }
    post {
        always {
            // Publishes the TestNG results chart in Jenkins
            testNG(reportFilenamePattern: '**/testng-results.xml')
            
            // Note: Playwright Java doesn't generate a "playwright-report" folder by default 
            // unless you've specifically configured a custom reporter in your code.
            // If you don't have this folder, you can remove or comment out this block.
            /*
            publishHTML(target: [
                reportDir: 'target/playwright-report', 
                reportFiles: 'index.html',
                reportName: 'Playwright Report'
            ])
            */
        }
    }
}
