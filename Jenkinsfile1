import java.nio.charset.StandardCharsets

/**
 * 只支持 REST API
 */
// get put 请求
def get_req(url) {
    def req = new URL(url).openConnection();
    req.setRequestProperty("Content-Type", "application/json")
    def resCode = req.getResponseCode();
    println("Request URL ${url}, ResponseCode: ${resCode}");
    def res = req.getInputStream()
    def resData = res.getText()
    if (resCode.equals(200)) {
        def header = req.getHeaderFields()
        println("Request URL ${url}, ResponseCode: ${resCode}, data: ${resData}");
        return [data: resData, header: header]
    } else {
        println("Request URL ${url} 请求失败了...  ${resData}")
    }
}


//定义 post put请求
def post_req(url, data, method = "POST") {
    def req = new URL(url).openConnection();
    req.setRequestMethod(method)
    req.setDoOutput(true)
    req.setRequestProperty("Content-Type", "application/json")
    byte[] message = data.getBytes(StandardCharsets.UTF_8)
    req.getOutputStream().write(message);
    def resCode = req.getResponseCode();
    println("Request URL ${url}, ResponseCode: ${resCode}");
    def res = req.getInputStream()
    def resData = res.getText()
    if (resCode.equals(200)) {
        def header = req.getHeaderFields()
        res.close()
        println("Request URL ${url}, ResponseCode: ${resCode}, data: ${resData}");
        return [data: resData, header: header]
    } else {
        println("Request URL ${url} 请求失败了...  ${resData}")
    }
}

def report_fr(id,status,message=""){
    // 根据上面的公共方法 远程调用 TODO

}
// 将文件提交到 资源仓库中
def commit_repository(file,location){
    bat 'git add ' + location+file
    bat 'git commit'
    bat 'git push'
}

// 邮件发送
def email_notify(to,topic,body,file){
    // 邮件发送 内容
    emailext attachmentsPattern: file //附件中携带测试报告
    attachLog: true, // 附件中携带构建LOG
    // 邮件内容
    body: body,
    // 邮件接收人,culprits包含了近期提交人员和触发人员
    recipientProviders: [culprits()], subject: topic, to:to
}

pipeline {
    agent any

    stages {
        stage('Evn Setting'){
            steps {
                echo '环境准备，参数准备'
            }
        }
    }
    stages {
        stage('Evn Clean'){
            steps {
                echo '清除历史记录'
                deleteDir()
                echo '清除历史记录完成'
            }
        }
    }
    stages {
        stage('Code Download'){
            when {
              environment name: 'SCM_TYPE', value: 'SVN'
            }
            steps {
                // 如果是 Git 则忽略
                checkout([$class: 'SubversionSCM', additionalCredentials: [],
                excludedCommitMessages: '', excludedRegions: '', excludedRevprop: '',
                excludedUsers: '', filterChangelog: false, ignoreDirPropChanges: false, includedRegions: '',
                locations: [[cancelProcessOnExternalsFail: true, credentialsId: '', depthOption: 'infinity',
                ignoreExternalsOption: true, local: '.', remote: 'https://github.com/AypakNanot/AutoTest.git']],
                quietOperation: true, workspaceUpdater: [$class: 'UpdateUpdater']])
            }
        }
    }
    stages {
        stage('Code Check'){
            steps {
                echo 'code check'
                // 执行检查 生成报告
                bat 'mvn sonar:sonar -Dsonar.login=aa759d16938a8d092b31767cab3e9f49de790840'
                // 下载报告 到本地
                bat 'curl "http://192.198.31.55.7:9000/dashboard?id=org.sonarqube%3Asonarscanner-maven-basic" > report.html '
                script {
                    // 命令行 提交 报告 到 仓库
                    commit_repository('report.html','./check')
                    // 邮件通知
                    email_notify('lihua@optel.com.cn; 737121478@qq.com','Jenkins构建通知',"""
                    - $PROJECT_NAME - Build # $BUILD_NUMBER - $BUILD_STATUS!
                    """,'report.html')
                }
            }
        }
    }
    stages {
        stage('Package Version'){
            steps {
                echo '编译'
                // 每个项目都已经写好了，编译生成版本的脚本，执行即可.
                // bat 'build-pro.bat'
                build 'TMasterJob'
                echo '打包，生成版本'
                // 命令行 提交 报告 到 仓库
                commit_repository('OPTEL_TMaster2000_Flex_V8.00R2.00-*.zip','./version')
            }
        }
    }
    stages {
        stage('Deploy Version'){
            steps {
                echo '部署'
                // 项目部署实际情况部署，执行部署脚本
                bat 'deploy.bat'
                script {
                    // 生成环境报告 TODO
                    // 命令行 提交 报告 到 仓库
                    commit_repository('evn-report.txt','./env')
                }
            }
        }
    }
    stage('Exec Test') {
        steps {
            script {
                // 拉取测试用例
                // 调用命令，导入测试用例到中间件中
                // 根据开放的测试接口获取所有的测试用例，循环 进行测试
                bat encoding: 'UTF-8', script: 'curl "http://192.168.31.72:3000/api/open/run_auto_test?id=25&token=038db31806a013190fc332fce8bacc6e1b9d3ed4681e0905c331ef66f7ed02b2&env_7=DEV&mode=html&email=false&download=false" > %WORKSPACE%\\report\\report.html'
                echo '报告生成完成. %WORKSPACE%\\report\\report.html'
                // 修改状态或者上报fr
                report_fr('0045454','已解决','版本：Vxxxx测试通过.')
                // 命令行 将版本信息，系统信息，环境信息，生成报告 上传到仓库中 TODO
            }
        }
    }
    stage('Report') {
        steps {
            script {
                // 命令行 将报告 上传到仓库中 TODO
                bat 'zip %WORKSPACE%\\report %WORKSPACE%\\report.zip'
                // 提交测试报告到 配置管理服务器
                commit_repository('%WORKSPACE%\\report.zip')
                // 发送邮件通知
                email_notify('lihua@optel.com.cn; 737121478@qq.com','Test Notify',"""
                                    - $PROJECT_NAME - Build # $BUILD_NUMBER - $BUILD_STATUS!
                                    """,'%WORKSPACE%\\report.zip')
            }
        }
    }

    post {
        success {
            email_notify('lihua@optel.com.cn; 737121478@qq.com','Test Notify',"""
                                                - $PROJECT_NAME - Build # $BUILD_NUMBER - $BUILD_STATUS!
                                                - $DEFAULT_POSTSEND_SCRIPT
                                                """)
        }
        failure {
            email_notify('lihua@optel.com.cn','Test Notify',"""
                                            - $PROJECT_NAME - Build # $BUILD_NUMBER - $BUILD_STATUS!
                                            - $DEFAULT_POSTSEND_SCRIPT
                                            """)
        }
    }
}
