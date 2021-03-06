pipeline {
    agent any

    stages {
        stage('Checkout') {
            steps {
            /* groovylint-disable-next-line LineLength */
            checkout([$class: 'GitSCM', branches: [[name: '*/master']], extensions: [], userRemoteConfigs: [[url: 'https://github.com/jecausey/ec2.git']]])
            }
        }

        stage ('terraform init') {
            steps {
                /* groovylint-disable-next-line DuplicateStringLiteral */
                sh ('terraform init')
            }
        }

        stage ('terraform Action') {
            steps {
                /* groovylint-disable-next-line LineLength */
                script { properties([parameters([choice(choices: ['apply', 'destroy'], description: 'Choose Apply or Destroy?', name: 'action')])])
                }

                withCredentials([[
                $class: 'AmazonWebServicesCredentialsBinding',
                credentialsId: 'jenkins_terraform',
                accessKeyVariable: 'AWS_ACCESS_KEY_ID',
                secretKeyVariable: 'AWS_SECRET_ACCESS_KEY'
                ]])

                {
                echo "Terraform action is --> ${action}"
                /* groovylint-disable-next-line GStringExpressionWithinString */
                sh ('terraform ${action} --auto-approve')
                }
            }
        }
    }
}
