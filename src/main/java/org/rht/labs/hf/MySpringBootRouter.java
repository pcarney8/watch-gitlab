package org.rht.labs.hf;

import org.apache.camel.model.rest.RestBindingMode;
import org.apache.camel.spring.boot.FatJarRouter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class MySpringBootRouter extends FatJarRouter {

    @Value("${CAMEL_PORT:8080}")
    String port;
    @Value("${CAMEL_COMPONENT:servlet}")
    String restComponent;
    @Value("${HF_GITLAB:http://gitlab-to-send-webhook.com/someRepo}")
    String remoteHfGitlab;
    @Value("${CAMEL_COMPONENT:https://gitlab-to-receive-update.com/theOtherRepoToSync}")
    String rhconsultingGitlab;


    private String syncRouteUri = "direct:sync-route";

    public void configure() throws Exception {
        restConfiguration().component(restComponent).port(port).bindingMode(RestBindingMode.off)
                .contextPath("/api")
        ;

        rest("").id("gitlab-endpoint").description("Gitlab Enterprise API")
                // get organization information
                .post().id("github-get-org-endpoint")
                .description("Sync gitlab")
                .to(syncRouteUri)
        ;


        from(syncRouteUri)
                .to("git:///tmp/testRepo?operation=pull&remotePath=" + remoteHfGitlab)
                .to("git:///tmp/testRepo?operation=push&remotePath=" + rhconsultingGitlab)
        ;
    }
}
