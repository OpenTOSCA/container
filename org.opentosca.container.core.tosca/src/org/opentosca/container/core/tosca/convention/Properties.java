package org.opentosca.container.core.tosca.convention;

public class Properties {

    // old properties now
    public static final String OPENTOSCA_DECLARATIVE_PROPERTYNAME_SERVERIP = "ServerIP";
    public static final String OPENTOSCA_DECLARATIVE_PROPERTYNAME_INSTANCEID = "InstanceId";
    public static final String OPENTOSCA_DECLARATIVE_PROPERTYNAME_SSHUSER = "SSHUser";
    public static final String OPENTOSCA_DECLARATIVE_PROPERTYNAME_SSHPRIVATEKEY = "SSHPrivateKey";

    // new properties
    public static final String OPENTOSCA_DECLARATIVE_PROPERTYNAME_VMIP = "VMIP";
    public static final String OPENTOSCA_DECLARATIVE_PROPERTYNAME_VMINSTANCEID = "VMInstanceID";
    public static final String OPENTOSCA_DECLARATIVE_PROPERTYNAME_VMLOGINNAME = "VMUserName";
    public static final String OPENTOSCA_DECLARATIVE_PROPERTYNAME_VMLOGINPASSWORD = "VMPrivateKey";
    public static final String OPENTOSCA_DECLARATIVE_PROPERTYNAME_RASPBIANIP = "IP";
    public static final String OPENTOSCA_DECLARATIVE_PROPERTYNAME_RASPBIANUSER = "User";
    public static final String OPENTOSCA_DECLARATIVE_PROPERTYNAME_RASPBIANPASSWD = "Password";

    public static final String OPENTOSCA_DECLARATIVE_PROPERTYNAME_CONTAINERIP = "ContainerIP";
    public static final String OPENTOSCA_DECLARATIVE_PROPERTYNAME_DOCKERENGINEURL = "DockerEngineURL";

    // serverless function properties
    public static final String OPENTOSCA_DECLARATIVE_PROPERTYNAME_FUNCTIONNAME = "FunctionName";
    public static final String OPENTOSCA_DECLARATIVE_PROPERTYNAME_RUNTIME = "Runtime";
    public static final String OPENTOSCA_DECLARATIVE_PROPERTYNAME_FUNCTIONURL = "FunctionURL";
    // timer event properties
    public static final String OPENTOSCA_DECLARATIVE_PROPERTYNAME_EVENTNAME = "EventName";
    public static final String OPENTOSCA_DECLARATIVE_PROPERTYNAME_CRON = "CRON";
    // http event properties
    public static final String OPENTOSCA_DECLARATIVE_PROPERTYNAME_CREATEHTTPEVENT = "CreateHTTPEvent";
    public static final String OPENTOSCA_DECLARATIVE_PROPERTYNAME_APIID = "APIID";
    public static final String OPENTOSCA_DECLARATIVE_PROPERTYNAME_RESOURCEID = "ResourceID";
    public static final String OPENTOSCA_DECLARATIVE_PROPERTYNAME_HTTPMETHOD = "HTTPMethod";
    public static final String OPENTOSCA_DECLARATIVE_PROPERTYNAME_AUTHTYPE = "AuthorizationType";
    public static final String OPENTOSCA_DECLARATIVE_PROPERTYNAME_FUNCTIONURI = "FunctionURI";
    // database event properties
    public static final String OPENTOSCA_DECLARATIVE_PROPERTYNAME_DATABASENAME = "DatabaseName";
    public static final String OPENTOSCA_DECLARATIVE_PROPERTYNAME_TYPEOFCHANGE = "ListenToWhatChanges";
    public static final String OPENTOSCA_DECLARATIVE_PROPERTYNAME_DATABASEHOSTURL = "DatabaseHostUrl";
    public static final String OPENTOSCA_DECLARATIVE_PROPERTYNAME_DATABASEUSER = "DatabaseUsername";
    public static final String OPENTOSCA_DECLARATIVE_PROPERTYNAME_DATABASEPW = "DatabasePassword";
    public static final String OPENTOSCA_DECLARATIVE_PROPERTYNAME_STARTPOS = "StartingPosition";
    // blobstorage event properties
    public static final String OPENTOSCA_DECLARATIVE_PROPERTYNAME_BUCKETNAME = "BucketName";
    public static final String OPENTOSCA_DECLARATIVE_PROPERTYNAME_EVENTTYPE = "EventType";
    // pubsub message properties
    public static final String OPENTOSCA_DECLARATIVE_PROPERTYNAME_TOPIC = "TopicName";
    public static final String OPENTOSCA_DECLARATIVE_PROPERTYNAME_MESSAGEHUBINSTANCE = "MessageHubInstanceName";

}
