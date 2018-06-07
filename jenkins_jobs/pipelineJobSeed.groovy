pipelineJob("sample-app-pipeline-job") {
    definition {
        cpsScm {
            scm {
                git {
                    remote {
                        url('https://github.com/nuclear0wl/epmjnkns.git')
                        credentials('nuclear0wl-github')
                    }
                    branch('master')
                }
            }
            scriptPath("jenkins_jobs/pipelineJob.groovy")
        }
    }
}
