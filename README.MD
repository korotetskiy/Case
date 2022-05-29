	# --------------------------------------------------------------------------------------------------
	# Author: Vladimir Korotetskiy
	# To start the system, need to specify the correct data:
	# 1) name instead of mysite.test.com, test.com
	# 2) ip4 instead of 192.168.0.1/16
	# 3) VPCId: instead of vpc-test1234
	# 4) VPCRegion: instead of eu-central-1
	# 5) Locations instead of EU
	# 6) S3-BUCKET_NAME
	# The system must be started from an account that has the infrastructure with VPC deployed.
	# --------------------------------------------------------------------------------------------------
    AWSTemplateFormatVersion:
    Description: AWS CloudFormation Template S3_With_CloudFront_Distribution

    Parameters:
    DomainName:
    Type: String    
    Description: The DNS name of an existing Amazon Route 53 hosted zone
    
    HostedZoneId:
    Type: String    
    Description: Amazon Route53 Hosted Zone Id for DomainName
    
    BucketName:
    Type: String    
    Description: Amazon S3 files bucket name
    
    CertificateArn:
    Type: String    
    Description: Certificate Link
    
    bucketname: 
    Type: String
    Description: Test          
    
     cannonicalid:
     Type : String
          Description: 1245...       
        
    Conditions:
        CreateProdResources: !Equals [!Ref EnvType dev]
    
    Resources:
        testbucket: 
          Type: AWS::S3::Bucket
          Properties:       
            BucketName: !Ref bucketname          
            WebsiteConfiguration:
               IndexDocument: index.html              
     
        mybucketpolicy: 
           Type : AWS::S3::BucketPolicy
           Properties : 
              PolicyDocument : 
                 Id : MyPolicy
                 Statement :  
                    Sid : Grant a CloudFront Origin Identity access to support private content
                    Action : [ s3:GetObject ]
                    Effect : Deny
                    Resource :  Fn::Join: 
                           [ arn:aws:s3:::  !Ref: testbucket   /* ]
                    Principal : 
                       CanonicalUser: !Ref cannonicalid 
                                  
              Bucket:  !Ref  testbucket 
              
           testdistribution : 
            Type : AWS::CloudFront::Distribution
            Properties: 
               DistributionConfig : 
                   Origins : 
                           Id : S3Origin
                         - DomainName:  Fn::GetAtt: [ testbucket test.com ] 
                           S3OriginConfig: 
                               OriginAccessIdentity : origin-access-identity/cloudfront/TEST
                       
                   Enabled: true
                   Comment: 
                   DefaultRootObject : index.html                    
                   Aliases: 
                       - mysite.test.com
                   CacheBehaviors: 
                            TargetOriginId: S3Origin
                            ForwardedValues: 
                                QueryString: false
                            ViewerProtocolPolicy: HTTPS_ONLY
                            Restrictions:
                            GeoRestriction:
                            RestrictionType: whitelist
                            Locations:
                            - EU
                            ViewerCertificate:
                            CloudFrontDefaultCertificate: 'true'
                   CertificateArn: string
	           IsDefault: true
			SampleBucketPolicy:
			  Type: AWS::S3::BucketPolicy
			  Properties:
			       Version: 
			Statement:
			      Effect: Deny
			      Principal: *
			      Action: s3:*
			      Resource:
			         arn:aws:s3:::MyExampleBucket/*
             #Allow for CloudFront
                         Id: PolicyForCloudFrontPrivateContent
                         Statement:
                         - Effect: Allow
                           Principal:
                           AWS: arn:aws:iam::cloudfront:user/CloudFront Origin Access Identity TEST123456
                           Action:
                            - s3:GetObject
                            - s3:PutObject
                           Resource: arn:aws:s3:::*

             DNS:
                    Type: AWS::Route53::HostedZone
                           Properties:
				    HostedZoneConfig:
				    HostedZoneTags:
				    Name: test.com
				    VPCs:
				    - VPCId: vpc-test1234
				      VPCRegion: eu-central-1
				     HostedZoneTags:
				    - Key: SampleKey1
				      Value: SampleValue1
  
					myDNSRecord:
					  HostedZoneId: 
					    Name: mysite.test.com.
					    Type: A
					    TTL: '900'
					    ResourceRecords:
					    - 'v=spf1 ip4:192.168.0.1/16 -all'
                      
          Outputs:
           ID CloudFront:
           Description: The Created ID CloudFront
           Value: !GetAtt DistributionList.Items[*].id:Idorigin:Origins.Items[0].Id[?origin=='S3-BUCKET_NAME'].id*
         Export:
         Name: !Sub $AWS::CloudFront::Distribution-ID CloudFront
