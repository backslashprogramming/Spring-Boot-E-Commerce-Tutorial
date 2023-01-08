package com.youtube.tutorial.ecommercebackend.api.security;

import com.youtube.tutorial.ecommercebackend.model.LocalUser;
import com.youtube.tutorial.ecommercebackend.service.UserService;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.SimpMessageType;
import org.springframework.messaging.simp.config.ChannelRegistration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.security.authorization.AuthorizationEventPublisher;
import org.springframework.security.authorization.AuthorizationManager;
import org.springframework.security.authorization.SpringAuthorizationEventPublisher;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.messaging.access.intercept.AuthorizationChannelInterceptor;
import org.springframework.security.messaging.access.intercept.MessageMatcherDelegatingAuthorizationManager;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

import java.util.Map;

/**
 * Class to configur spring websockets.
 */
@Configuration
@EnableWebSocket
@EnableWebSocketMessageBroker
public class WebsocketConfiguration
    implements WebSocketMessageBrokerConfigurer {

  /** The Application Context. */
  private ApplicationContext context;
  /** The JWT Request Filter. */
  private JWTRequestFilter jwtRequestFilter;
  /** The User Service. */
  private UserService userService;
  /** Matcher instance. */
  private static final AntPathMatcher MATCHER = new AntPathMatcher();

  /**
   * Default constructor for spring injection.
   * @param context
   * @param jwtRequestFilter
   * @param userService
   */
  public WebsocketConfiguration(ApplicationContext context,
                                JWTRequestFilter jwtRequestFilter,
                                UserService userService) {
    this.context = context;
    this.jwtRequestFilter = jwtRequestFilter;
    this.userService = userService;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void registerStompEndpoints(StompEndpointRegistry registry) {
    registry.addEndpoint("/websocket").setAllowedOriginPatterns("**").withSockJS();
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void configureMessageBroker(MessageBrokerRegistry registry) {
    registry.enableSimpleBroker("/topic");
    registry.setApplicationDestinationPrefixes("/app");
  }

  /**
   * Creates an AuthorizationManager for managing authentication required for
   * specific channels.
   * @return The AuthorizationManager object.
   */
  private AuthorizationManager<Message<?>> makeMessageAuthorizationManager() {
    MessageMatcherDelegatingAuthorizationManager.Builder messages =
        new MessageMatcherDelegatingAuthorizationManager.Builder();
    messages.
        simpDestMatchers("/topic/user/**").authenticated()
        .anyMessage().permitAll();
    return messages.build();
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void configureClientInboundChannel(ChannelRegistration registration) {
    AuthorizationManager<Message<?>> authorizationManager =
        makeMessageAuthorizationManager();
    AuthorizationChannelInterceptor authInterceptor =
        new AuthorizationChannelInterceptor(authorizationManager);
    AuthorizationEventPublisher publisher =
        new SpringAuthorizationEventPublisher(context);
    authInterceptor.setAuthorizationEventPublisher(publisher);
    registration.interceptors(jwtRequestFilter, authInterceptor,
        new RejectClientMessagesOnChannelsChannelInterceptor(),
        new DestinationLevelAuthorizationChannelInterceptor());
  }

  /**
   * Interceptor for rejecting client messages on specific channels.
   */
  private class RejectClientMessagesOnChannelsChannelInterceptor
      implements ChannelInterceptor {

    /** Paths that do not allow client messages. */
    private String[] paths = new String[] {
        "/topic/user/*/address"
    };

    /**
     * {@inheritDoc}
     */
    @Override
    public Message<?> preSend(Message<?> message, MessageChannel channel) {
      if (message.getHeaders().get("simpMessageType").equals(SimpMessageType.MESSAGE)) {
        String destination = (String) message.getHeaders().get(
            "simpDestination");
        for (String path: paths) {
          if (MATCHER.match(path, destination))
            message = null;
        }
      }
      return message;
    }

  }

  /**
   * Interceptor to apply authorization and permissions onto specific
   * channels and path variables.
   */
  private class DestinationLevelAuthorizationChannelInterceptor
      implements ChannelInterceptor {

    /**
     * {@inheritDoc}
     */
    @Override
    public Message<?> preSend(Message<?> message, MessageChannel channel) {
      if (message.getHeaders().get("simpMessageType").equals(SimpMessageType.SUBSCRIBE)) {
        String destination = (String) message.getHeaders().get(
            "simpDestination");
        String userTopicMatcher = "/topic/user/{userId}/**";
        if (MATCHER.match(userTopicMatcher, destination)) {
          Map<String, String> params = MATCHER.extractUriTemplateVariables(
              userTopicMatcher, destination);
          try {
            Long userId = Long.valueOf(params.get("userId"));
            Authentication authentication =
                SecurityContextHolder.getContext().getAuthentication();
            if (authentication != null) {
              LocalUser user = (LocalUser) authentication.getPrincipal();
              if (!userService.userHasPermissionToUser(user, userId)) {
                message = null;
              }
            } else {
              message = null;
            }
          } catch (NumberFormatException ex) {
            message = null;
          }
        }
      }
      return message;
    }
  }
}
