package org.wcss.keycloak;

import java.util.Arrays;
import java.util.Map;
import java.util.stream.*;
import java.util.List;

import org.jboss.logging.Logger;
import org.keycloak.authentication.AuthenticationFlowContext;
import org.keycloak.authentication.AuthenticationFlowError;
import org.keycloak.authentication.Authenticator;
import org.keycloak.models.*;

import jakarta.ws.rs.core.Response;

public class MembershipAuthenticator implements Authenticator {

    private static final Logger logger = Logger.getLogger(MembershipAuthenticator.class);

    @Override
    public void authenticate(AuthenticationFlowContext context) {
        UserModel user = context.getUser();

	AuthenticatorConfigModel configModel = context.getAuthenticatorConfig();
        Map<String, String> config = configModel.getConfig();

        Stream<GroupModel> userGroups = user.getGroupsStream();
        List<String> allowedGroups = getAllowedGroups(context, config);

	// Cannot use user.isMemberOf() cause mapping a String group name to a GroupModel is inefficient
	Boolean isAllowedMember = userGroups.map(GroupModel::getName).anyMatch(allowedGroups::contains);

	if (isAllowedMember) {
		context.success();
	} else {
		String errorMsg = config.get(MembershipAuthenticatorFactory.ERROR_MSG);
		Response response = context.form().setError(errorMsg).createErrorPage(Response.Status.FORBIDDEN);
		logger.warnf("Authentication failed. User %s is not a member of the allowed groups: %s", user.getUsername(), allowedGroups);
		context.getEvent().error("invalid_groups");
		context.failure(AuthenticationFlowError.ACCESS_DENIED, response, "User is not a member of the allowed groups.", errorMsg);
	}
    }

    private List<String> getAllowedGroups(AuthenticationFlowContext context, Map<String, String> config) {
	String groups_string = config.get(MembershipAuthenticatorFactory.ALLOWED_GROUPS);
        List<String> groups = Arrays.asList(groups_string.split("##"));
	logger.debugf("Allowed groups unparsed: %s", groups_string);
	logger.debugf("Allowed groups parsed: %s", groups);
	return groups;    
    }

    @Override
    public void action(AuthenticationFlowContext context) {
    }

    @Override
    public boolean requiresUser() {
        return true;
    }

    @Override
    public boolean configuredFor(KeycloakSession session, RealmModel realm, UserModel user) {
        return true;
    }

    @Override
    public void setRequiredActions(KeycloakSession session, RealmModel realm, UserModel user) {
    }

    @Override
    public void close() {
    }

}
