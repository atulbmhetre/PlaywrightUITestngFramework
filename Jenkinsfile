pipeline {
    agent any
    tools {
        nodejs 'Node25' // Match the name exactly as it appears in your log
        maven 'M3'
    }
    stages {
        stage('Install Dependencies') {
            steps {
                // Use 'bat' for Windows instead of 'sh'
                bat 'npm install'
                bat 'npx playwright install --with-deps'
            }
        }
        stage('Run Tests') {
            steps {
                // If using TestNG with Maven:
                bat 'mvn test' 
                
                // OR if you are running playwright directly:
                // bat 'npx playwright test'
            }
        }
    }
    post {
        always {
            // This publishes your TestNG results
            testNG(reportFilenamePattern: '**/testng-results.xml')
            
            // This publishes the Playwright HTML report
            publishHTML(target: [
                reportDir: 'playwright-report',
                reportFiles: 'index.html',
                reportName: 'Playwright Report',
                alwaysLinkToLastBuild: true,
                keepAll: true
            ])
        }
    }
}
