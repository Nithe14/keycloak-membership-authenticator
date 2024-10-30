package org.wcss.keycloak;

import static org.keycloak.provider.ProviderConfigProperty.MULTIVALUED_STRING_TYPE;
import static org.keycloak.provider.ProviderConfigProperty.TEXT_TYPE;

import java.util.ArrayList;
import java.util.List;

import org.keycloak.Config;
import org.keycloak.authentication.Authenticator;
import org.keycloak.authentication.AuthenticatorFactory;
import org.keycloak.models.AuthenticationExecutionModel;
import org.keycloak.models.KeycloakSession;
import org.keycloak.models.KeycloakSessionFactory;
import org.keycloak.provider.ProviderConfigProperty;

public class MembershipAuthenticatorFactory implements AuthenticatorFactory {

    public static final String ID = "membership-authenticator";

    static final String ALLOWED_GROUPS = "allowed_groups";
    static final String ERROR_MSG = "error_message";

    @Override
    public Authenticator create(KeycloakSession keycloakSession) {
	return new MembershipAuthenticator();
    }

    @Override
    public String getDisplayType() {
        return "Membership Authenticator";
    }

    @Override
    public boolean isConfigurable() {
        return true;
    }

    @Override
    public AuthenticationExecutionModel.Requirement[] getRequirementChoices() {
        return new AuthenticationExecutionModel.Requirement[] { AuthenticationExecutionModel.Requirement.REQUIRED };
    }

    @Override
    public boolean isUserSetupAllowed() {
        return false;
    }

    @Override
    public String getHelpText() {
        return "Limits access to only allowed groups";
    }

    @Override
    public List<ProviderConfigProperty> getConfigProperties() {
	List<ProviderConfigProperty> properties = new ArrayList<ProviderConfigProperty>();

        ProviderConfigProperty allowedGroups = new ProviderConfigProperty();
        allowedGroups.setType(MULTIVALUED_STRING_TYPE);
        allowedGroups.setName(ALLOWED_GROUPS);
	allowedGroups.setRequired(true);
        allowedGroups.setLabel("Allowed Groups");
        allowedGroups.setHelpText("Only users in one of these groups will be authenticated.");
	properties.add(allowedGroups);

	ProviderConfigProperty errorMsg = new ProviderConfigProperty();
	errorMsg.setType(TEXT_TYPE);
	errorMsg.setName(ERROR_MSG);
	errorMsg.setLabel("Error Message for Rejected Users");
	errorMsg.setHelpText("A specific error message displayed when a user is not allowed access.");
	properties.add(errorMsg);

	return properties;
    }

    @Override
    public String getReferenceCategory() {
        return null;
    }

    @Override
    public void init(Config.Scope scope) {
    }

    @Override
    public void postInit(KeycloakSessionFactory keycloakSessionFactory) {
    }

    @Override
    public void close() {
    }

    @Override
    public String getId() {
        return ID;
    }
}
