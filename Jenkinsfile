pipeline {
    agent any

    stages {
        stage('拉取git代码') {
            steps {
                echo '拉取git代码 - SUCCESS'
            }
        }
        stage('通过maven构建项目') {
            steps {
                echo '通过maven构建项目 - SUCCESS'
            }
        }
        stage('通过SONAR进行质量检测') {
            steps {
                echo '通过SONAR进行质量检测 - SUCCESS'
            }
        }
        stage('制作镜像') {
            steps {
                echo '制作镜像 - SUCCESS'
            }
        }
        stage('将镜像推送到Harbor仓库中') {
            steps {
                echo '将镜像推送到Harbor仓库中 -SUCCESS'
            }
        }
        stage('运行') {
            steps {
                echo 'Hello World'
            }
        }
    }
}
