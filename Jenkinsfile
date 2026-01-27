pipeline {
    agent any
    tools {
        // Match the exact names you gave in Jenkins Global Tool Configuration
        nodejs 'Node25' 
        maven 'M3' 
    }
    stages {
        stage('Run Playwright Tests') {
            steps {
                    script {
                        // Map the friendly name to the actual file path
                        def suiteMap = [
                            'Smoke': 'src/test/resources/testng_smoke.xml',
                            'Regression': 'src/test/resources/testng_regression.xml'
                            'crossbrowser': 'src/test/resources/testng_crossbrowser.xml'
                        ]
                        def selectedSuite = suiteMap[params.suiteFile]

                        bat "mvn clean test -DsuiteFile=${selectedSuite} -Denv=${params.env} -Dbrowser=${params.browser}"
                    }
            }
        }
    }
    post {
        always {

            // Publish clickable HTML report
            publishHTML([
                allowMissing: false,
                alwaysLinkToLastBuild: true,
                keepAll: true,
                reportDir: 'test-output/Test-ExtentReport',
                reportFiles: '*.html',
                reportName: 'Playwright Extent Report'
            ])

            // Archive full test-output folder
            bat 'dir /s test-output'
            archiveArtifacts artifacts: 'test-output/Test-ExtentReport/**/*.*', fingerprint: true
        }
    }


}
