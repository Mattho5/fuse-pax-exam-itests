package org.mattho.fuse.tests.paxexamtests.fabricitests;


import org.apache.activemq.ActiveMQConnectionFactory;
import org.apache.activemq.command.DiscoveryEvent;
import org.apache.activemq.transport.discovery.DiscoveryListener;
import org.apache.curator.framework.CuratorFramework;
import org.apache.karaf.tooling.exam.options.DoNotModifyLogOption;
import org.apache.karaf.tooling.exam.options.KarafDistributionOption;
import io.fabric8.api.Container;
import io.fabric8.api.FabricService;
import io.fabric8.itests.paxexam.support.*;
import io.fabric8.itests.paxexam.support.FabricTestSupport;
import org.fusesource.mq.fabric.FabricDiscoveryAgent;
import org.junit.Assert;
import org.junit.Test;
import org.junit.rules.Timeout;
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

import javax.jms.*;
import java.io.File;
import java.util.Collection;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import static org.apache.karaf.tooling.exam.options.KarafDistributionOption.*;
import static org.junit.Assert.assertTrue;

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

    private FabricService fabricService;

    //@Inject
    // protected BundleContext bundleContext;

   /* @Configuration
    public Option[] config() {

        return new Option[]{ KarafDistributionOption.karafDistributionConfiguration().frameworkUrl(
                CoreOptions.maven().groupId("io.fabric8").artifactId("fuse-fabric").type("zip").versionAsInProject())
                .karafVersion(MavenUtils.getArtifactVersion("io.fabric8", "fuse-fabric")).name("JBoss Fuse").unpackDirectory(new File("target/exam")),
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

                mavenBundle("io.fabric8.itests", "fabric-itests-common", MavenUtils.getArtifactVersion("io.fabric8.itests", "fabric-itests-common")),
                mavenBundle("org.fusesource.tooling.testing", "pax-exam-karaf", MavenUtils.getArtifactVersion("org.fusesource.tooling.testing", "pax-exam-karaf")),

                new DoNotModifyLogOption(),
                keepRuntimeFolder(),
                //   new DoNotModifyLogOption(),

        };


    }      */


    @Configuration
         public Option[] config() {


        return new Option[]{
              //  new DefaultCompositeOption(fabricDistributionConfiguration()),
             //   mavenBundle("org.apache.activemq","activemq-all",MavenUtils.getArtifactVersion("org.apache.activemq","activemq-all")) ,
            //    mavenBundle("org.jboss.amq","mq-fabric",MavenUtils.getArtifactVersion("org.jboss.amq","mq-fabric"))
        KarafDistributionOption.karafDistributionConfiguration().frameworkUrl(
              CoreOptions.maven().groupId("org.jboss.amq").artifactId("jboss-a-mq").type("zip").versionAsInProject())
                .karafVersion(MavenUtils.getArtifactVersion("org.jboss.amq", "jboss-a-mq")).name("JBoss A-MQ").unpackDirectory(new File("target/exam")),
                //bundles for testing

                useOwnExamBundlesStartLevel(50),


                editConfigurationFilePut("etc/config.properties", "karaf.startlevel.bundle", "50"),
                editConfigurationFilePut("etc/config.properties", "karaf.startup.message", "Loading Fuse from: ${karaf.home}"),
                editConfigurationFilePut("etc/users.properties", "admin", "admin,admin"),
                mavenBundle("io.fabric8.itests", "fabric-itests-common", MavenUtils.getArtifactVersion("io.fabric8.itests", "fabric-itests-common")),
                mavenBundle("org.fusesource.tooling.testing", "pax-exam-karaf", MavenUtils.getArtifactVersion("org.fusesource.tooling.testing", "pax-exam-karaf")),
           //     mavenBundle("org.fusesource.mq","mq-fabric",MavenUtils.getArtifactVersion("org.fusesource.mq","mq-fabric")),
              //  mavenBundle("javax.jms","",MavenUtils.getArtifactVersion("org.jboss.amq","mq-fabric")),
             /*   mavenBundle("org.apache.geronimo.specs","geronimo-jms_1.1_spec",MavenUtils.getArtifactVersion("org.apache.geronimo.specs","geronimo-jms_1.1_spec")),
                mavenBundle("org.apache.geronimo.specs","geronimo-annotation_1.1_spec",MavenUtils.getArtifactVersion("org.apache.geronimo.specs","geronimo-annotation_1.1_spec")),
                mavenBundle("org.apache.geronimo.specs","geronimo-j2ee-management_1.1_spec",MavenUtils.getArtifactVersion("org.apache.geronimo.specs","geronimo-j2ee-management_1.1_spec")),
                mavenBundle("org.apache.activemq","activemq-all",MavenUtils.getArtifactVersion("org.apache.activemq","activemq-all")),
                mavenBundle("org.apache.xbean","xbean-classloader",MavenUtils.getArtifactVersion("org.apache.xbean","xbean-classloader")),
                mavenBundle("org.apache.xbean","xbean-spring",MavenUtils.getArtifactVersion("org.apache.xbean","xbean-spring")),
                mavenBundle("org.springframework","spring-beans",MavenUtils.getArtifactVersion("org.springframework","spring-beans")),
                mavenBundle("org.springframework","spring-context",MavenUtils.getArtifactVersion("org.springframework","spring-context")),
                mavenBundle("org.jboss.amq","mq-fabric",MavenUtils.getArtifactVersion("org.jboss.amq","mq-fabric")), */
             //   maven


                 };
         }






    @Test
    public void fabricRegistryTest() throws Exception {
        initFabric();
        initReplicatedNodes("child",3,"replicated");

      /*  System.out.println(executeCommand( "fabric:create -n"));

        System.out.println("Creating connection factory ");
        ConnectionFactory cf=new ActiveMQConnectionFactory("admin","admin","discovery:(fabric:replicated)");


        Connection c= cf.createConnection();
        c.start();

        Session s=c.createSession(false, Session.AUTO_ACKNOWLEDGE);

        Queue q=s.createQueue("fabricQueue");
        System.out.println("Trying to send message ");
        MessageProducer mp= s.createProducer(q);
        for(int i=0;i<100;i++)
            mp.send(s.createTextMessage("FABRIC"));


        System.out.println("Message was sended!!! ");
        Thread.sleep(1000);
        mp.close();

        MessageConsumer mc=s.createConsumer(q);
        TextMessage message=  (TextMessage)mc.receive(5000);
        int received=0;
        while(message!=null)  {
          received++;
         String m=message.getText();
         System.out.println("ReceivedMessage "+m);
          //  Assert.assertEquals("FABRIC",m);
            message= (TextMessage)mc.receive(5000);
        }


        mc.close();
        s.close();
        c.close();

        Assert.assertEquals(received,100);        */
    }


    private void initFabric(){
        System.out.println(executeCommand( "fabric:create -n"));
        System.out.println("WAITING FOR FABRIC");
        fabricService=getFabricService();
        Assert.assertNotNull(fabricService);

    }


     private void initReplicatedNodes(String name,int numberOfReplicas,String group) throws Exception {
        //String name="child";

       /* CuratorFramework curatorFramework = getCurator();
        final CountDownLatch serviceLatch = new CountDownLatch(1);
        final FabricDiscoveryAgent discoveryAgent = new FabricDiscoveryAgent();
                                                                                     */
        System.out.println("CREATING CONTAINER");
        ChildContainerBuilder.child(numberOfReplicas).assertProvisioningResult().withName(name).build();

        System.out.println(executeCommand("fabric:container-list"));
        //   f.get
      /*
        discoveryAgent.start();
        discoveryAgent.setCurator(curatorFramework);
        discoveryAgent.setGroupName("replicatedBroker");
        discoveryAgent.setDiscoveryListener( new DiscoveryListener() {
            @Override
            public void onServiceAdd(DiscoveryEvent discoveryEvent) {
                System.out.println("===============Service added:" + discoveryEvent.getServiceName());
                serviceLatch.countDown();
            }

            @Override
            public void onServiceRemove(DiscoveryEvent discoveryEvent) {
                System.out.println("===================Service removed:" + discoveryEvent.getServiceName());
            }
        });


           -*/
        System.out.println(executeCommand("osgi:list"));

        String containers=getContainerList(name,numberOfReplicas);
        System.out.println(executeCommand(" mq-create --parent-profile mq-replicated --group "+group+" --assign-container "+containers+" brokers"));
        System.out.println(executeCommand("fabric:container-list"));

        Thread.sleep(3000);
        for(int i=1;i<=numberOfReplicas;i++)
            waitForProvision(name+i);


       // Thread.sleep(10000);
      //  assertTrue(serviceLatch.await(40, TimeUnit.SECONDS));
        System.out.println("waiting for cluster");
        Thread.sleep(120000);
        System.out.println(executeCommand("fabric:cluster-list"));
        //f.a

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
    private void waitForProvision(Container c) throws InterruptedException {
        while(!c.isProvisioningComplete()){
            System.out.println(c.getProvisionResult()+"! "+c.getProvisionStatus()+" "+c.getResolver());
            Thread.sleep(1000);
        }
    }


    private boolean provisionSuccess(Container con){
        if(con.isProvisioningComplete())
            return true;
        else return false;


    }

    /**
     * returning String which contains list of container
     * @param name
     * @param number
     * @return
     */
    private static String getContainerList(String name,int number){
        String result="";
        for(int i=1;i<=number;i++){
            result+=name+i;
            if(i<number)
                result+=",";
        }
        return result;
    }

    private String whoIsMaster(){
       Container c = fabricService.getContainer("child1");
      //  c.get
      return "";
    }


    public static void main(String[] args){

        System.out.println(getContainerList("child",5));
    }




}

