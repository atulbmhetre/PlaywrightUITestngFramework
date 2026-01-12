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
            publishHTML([
                allowMissing: false,
                alwaysLinkToLastBuild: true,
                keepAll: true,
                reportDir: 'test-output/extent-reports',
                reportFiles: 'index.html',
                reportName: 'Playwright Extent Report'
            ])
        }
    }

}
