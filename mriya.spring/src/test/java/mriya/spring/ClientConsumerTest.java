package mriya.spring;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

public class ClientConsumerTest {

	public static void main(String[] args) {

		ApplicationContext ctx = new ClassPathXmlApplicationContext(
				"client.xml");
		Hello hello = (Hello) ctx.getBean("hello");
		
		hello.hello();

	}
}
