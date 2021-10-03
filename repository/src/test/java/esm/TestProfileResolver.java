package esm;

import org.springframework.core.env.AbstractEnvironment;
import org.springframework.test.context.ActiveProfilesResolver;

public class TestProfileResolver implements ActiveProfilesResolver {
    private static final String TEST_PROFILE = "test";

    @Override
    public String[] resolve(Class<?> testClass) {
        System.setProperty(AbstractEnvironment.ACTIVE_PROFILES_PROPERTY_NAME, TEST_PROFILE);
        return new String[] { TEST_PROFILE };
    }
}
