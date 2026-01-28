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
                    // 1. Exact map of Jenkins choice to Filename (no path, no .xml)
                    def suiteMap = [
                        'smoke'        : 'testng_smoke',
                        'regression'   : 'testng_regression',
                        'crossbrowser' : 'testng_crossbrowser'
                    ]

                    // 2. Lookup using 'params.testSuite' (Must match UI Name)
                    def testSuiteLower = params.testSuite.toLowerCase()
                    def selected = suiteMap[testSuiteLower]

                    if (selected == null) {
                        error "Selected suite '${params.testSuite}' not found in mapping!"
                    }

                    // 3. Logic: Only pass the -Dbrowser flag if NOT running crossbrowser
                    // and if the user didn't select 'All'
                    def browserFlag = ""
                    if (testSuiteLower != 'crossbrowser') {
                        if (params.browser != 'All') {
                            browserFlag = "-Dbrowser=${params.browser}"
                        }
                    } else {
                        echo "Cross-browser suite detected: Ignoring browser parameter to allow XML control."
                    }

                    // 4. Handover to Maven
                    bat "mvn clean test -DsuiteFile=${selected} -Denv=${params.env} ${browserFlag}"
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
