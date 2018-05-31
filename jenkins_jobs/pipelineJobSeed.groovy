pipelineJob("sample-app-pipeline-job") {
    definition {
        cpsScm {
            scm {
                git {
                    remote {
                        url('https://github.com/andrunah/epmjnkns.git')
                        credentials('jenkins-github')
                    }
                    branch('master')
                }
            }
            scriptPath("jenkins_jobs/pipelineJob.groovy")
        }
    }
}
