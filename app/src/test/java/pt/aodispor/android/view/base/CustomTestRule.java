//TODO NOT USED

package pt.aodispor.android.view.base;

import org.junit.rules.TestRule;
import org.junit.runner.Description;
import org.junit.runners.model.Statement;
import org.springframework.web.client.RestTemplate;

//import ...someApplication;

public class CustomTestRule implements TestRule {

    //private someApplication mApplication;
    private RestTemplate mMockRestTemplate;

    public CustomTestRule() {
        //mApplication = (someApplication) RuntimeEnvironment.application;

        // Configure mock dependencies
       // mMockRestTemplate = Mockito.mock(RestTemplate.class);
        //mApplication.setRestTemplate(mMockRestTemplate);
    }


    public RestTemplate getMockRestTemplate() {
        return mMockRestTemplate;
    }

    @Override
    public Statement apply(final Statement base, Description description) {
        return new Statement() {
            @Override
            public void evaluate() throws Throwable {
                base.evaluate();
            }
        };
    }
}
