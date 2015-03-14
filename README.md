# mriya
一个简单的rpc框架


##1 首先，需要加入jar 依赖
服务暴露mriya框架, 编程方式暴露服务
	<dependency>
		<groupId>mriya.provider</groupId>
		<artifactId>mriya.provider</artifactId>
	</dependency>
	
	服务暴露mriya框架，使用spring暴露服务
	<dependency>
		<groupId>mriya.spring</groupId>
		<artifactId>mriya.spring</artifactId>
	</dependency>
	

##2 短信发送服务客户端
	spring配置方式（注册中心优化中）
	
	<bean id="smsService" class="com.cmcc.mriya.provider.spring.MriyaSpringProxyFactory">
		<property name="api" value="com.cmcc.smsservice.api.ISMSenderService" />
		<property name="url" value="hessian://localhost:9090/smsservice" />
	</bean>



MriyaProxyFacotry factory = new MriyaProxyFacotry();
	ISMSenderService sms = (ISMSenderService) factory.create(
				ISMSenderService.class, "hessian://localhost:9090/"
						+ Constants.SMS_SERVICE);
 
 sms.sendSM(sms)
 
##3 短信发送服务端
 创建MriyaRpcExporter暴露服务，以及StandaloneServer 配置服务。

		 DemoSendCallback
			MriyaRpcExporter exporter = new MriyaRpcExporter("send", sendCallback,
				DemoSendCallback.class);

			new StandaloneServer().add(exporter).start(9090);
