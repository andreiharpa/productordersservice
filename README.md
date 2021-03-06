## Documentation
Swagger documentation vailable under http://localhost:8080/swagger-ui/#/


## You do not need to add authentication to your web service, but propose a protocol / method and justify your choice
Since we have built a REST API, we would need to maintain its stateless nature and thus, a session based auth would not suffice.
I would propose to use a JWT token based auth which can be easily implemented in Spring by leveraging spring security.


## How can you make the service redundant? What considerations should you do?
For a service to be reliable it should not be deployed on a single machine, but replicated and front-ended by a load balancer.
There are multiple deployment solutions available that leverage docker-compose. Using AWS fargate we can scale the service's tasks 
by employing autoscaling policies and can satisfy an increased load. Canary tests could be used to see weather each
instance is still responding or not.

While we covered redundancy regarding the Service, we also need to consider the database limitations.
With more complexity of our service and increased load, our dockerized DB might not handle and we might scale it above its limits.

#### Use a managed DB service
One solution to this problem would be to opt for a managed DB service such as AWS RDS which scales both vertically and horizontally.

#### Split into two microservices
Other solution would be to split our current service into two microservices having each their own database.
Each DB should be sufficiently easy to manage.
Microservice development isn't without its quirks. 
We would need to analyze our service requirements and optimize considering the
CAP (Consistency Availability Partition) triangle.