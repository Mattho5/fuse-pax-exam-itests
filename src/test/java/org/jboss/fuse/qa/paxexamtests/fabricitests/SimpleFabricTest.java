package org.jboss.fuse.qa.paxexamtests.fabricitests;


import org.apache.karaf.tooling.exam.options.DoNotModifyLogOption;
import org.apache.karaf.tooling.exam.options.KarafDistributionOption;
import org.fusesource.fabric.api.Container;
import org.fusesource.fabric.api.FabricService;
import org.fusesource.fabric.itests.paxexam.support.ChildContainerBuilder;
import org.fusesource.fabric.itests.paxexam.support.ContainerBuilder;
import org.fusesource.fabric.itests.paxexam.support.FabricTestSupport;
import org.fusesource.fabric.itests.paxexam.support.SshContainerBuilder;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.ops4j.pax.exam.CoreOptions;
import org.ops4j.pax.exam.MavenUtils;
import org.ops4j.pax.exam.Option;
import org.ops4j.pax.exam.junit.Configuration;
import org.ops4j.pax.exam.junit.ExamReactorStrategy;
import org.ops4j.pax.exam.junit.JUnit4TestRunner;
import org.ops4j.pax.exam.options.DefaultCompositeOption;
import org.ops4j.pax.exam.spi.reactors.AllConfinedStagedReactorFactory;
import org.osgi.framework.Bundle;

import java.io.File;

import static org.apache.karaf.tooling.exam.options.KarafDistributionOption.*;

/**
 * Created with IntelliJ IDEA.
 * User: mmelko
 * Date: 12/16/13
 * Time: 1:14 PM
 * To change this template use File | Settings | File Templates.
 */

@RunWith(JUnit4TestRunner.class)
@ExamReactorStrategy(AllConfinedStagedReactorFactory.class)
public class SimpleFabricTest extends FabricTestSupport {

    //@Inject
    // protected BundleContext bundleContext;

   /* @Configuration
    public Option[] config() {

        return new Option[]{ KarafDistributionOption.karafDistributionConfiguration().frameworkUrl(
                CoreOptions.maven().groupId("org.fusesource.fabric").artifactId("fuse-fabric").type("zip").versionAsInProject())
                .karafVersion(MavenUtils.getArtifactVersion("org.fusesource.fabric", "fuse-fabric")).name("JBoss Fuse").unpackDirectory(new File("target/exam")),
                //bundles for testing

                useOwnExamBundlesStartLevel(50),
                envAsSystemProperty(ContainerBuilder.CONTAINER_TYPE_PROPERTY, "child"),
                envAsSystemProperty(ContainerBuilder.CONTAINER_NUMBER_PROPERTY, "1"),
                envAsSystemProperty(SshContainerBuilder.SSH_HOSTS_PROPERTY),
                envAsSystemProperty(SshContainerBuilder.SSH_USERS_PROPERTY),
                envAsSystemProperty(SshContainerBuilder.SSH_PASSWORD_PROPERTY),
                envAsSystemProperty(SshContainerBuilder.SSH_RESOLVER_PROPERTY),

                editConfigurationFilePut("etc/config.properties", "karaf.startlevel.bundle", "50"),
                editConfigurationFilePut("etc/config.properties", "karaf.startup.message", "Loading Fabric from: ${karaf.home}"),
                editConfigurationFilePut("etc/users.properties", "admin", "admin,admin"),

                mavenBundle("org.fusesource.fabric.itests", "fabric-itests-common", MavenUtils.getArtifactVersion("org.fusesource.fabric.itests", "fabric-itests-common")),
                mavenBundle("org.fusesource.tooling.testing", "pax-exam-karaf", MavenUtils.getArtifactVersion("org.fusesource.tooling.testing", "pax-exam-karaf")),

                new DoNotModifyLogOption(),
                keepRuntimeFolder(),
                //   new DoNotModifyLogOption(),

        };


    }      */


    @Configuration
         public Option[] config() {
               return new Option[]{
                        new DefaultCompositeOption(fabricDistributionConfiguration()),
                };
         }






    @Test
    public void testContainerWithJvmOpts() throws Exception {
        System.out.println(executeCommand( "fabric:create -n"));
        System.out.println("WAITING FOR FABRIC");
        //      Thread.sleep(120000);
        FabricService f=getFabricService();
        Assert.assertNotNull(f);



        System.out.println("CREATING CONTAINER");
        ChildContainerBuilder.child(3).assertProvisioningResult().withName("child").build();
        System.out.println("LIST OF CONTAINERS");

        System.out.println(executeCommand(" mq-create --parent-profile mq-replicated --group broker --assign-container child1,child2,child3 brokers"));
        System.out.println(executeCommand("fabric:container-list"));

        Thread.sleep(3000);
         waitForProvision("child1");
         waitForProvision("child2");
         waitForProvision("child3");

        System.out.println(executeCommand("fabric:container-list"));
    }



    private boolean waitForProvision(String name) throws InterruptedException {
        boolean result=false;
        while(result==false){

            String res=executeCommand("fabric:container-list | grep "+name);
            System.out.println(res);
            if(res.contains("success"))
                result=true;
            else{
                Thread.sleep(1000);

            }

        }

        return result;
    }
    private boolean provisionSuccess(Container con){
        if(con.isProvisioningComplete())
            return true;
        else return false;


    }





}

