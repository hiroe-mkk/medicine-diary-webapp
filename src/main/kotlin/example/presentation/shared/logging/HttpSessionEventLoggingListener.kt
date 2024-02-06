package example.presentation.shared.logging

import jakarta.servlet.http.*
import org.slf4j.*
import org.springframework.stereotype.*

/**
 * セッションに関するログを出力する Listener
 */
@Component
class HttpSessionEventLoggingListener : HttpSessionListener,
                                        HttpSessionAttributeListener,
                                        HttpSessionActivationListener {
    private val logger = LoggerFactory.getLogger(HttpSessionEventLoggingListener::class.java)

    override fun sessionWillPassivate(sessionEvent: HttpSessionEvent) {
        if (logger.isDebugEnabled) {
            logger.debug("Session Will Passivate SESSION_ID=[{}] -> {}",
                         sessionEvent.session.id, sessionEvent.source)
        }
    }

    override fun sessionDidActivate(sessionEvent: HttpSessionEvent) {
        if (logger.isDebugEnabled) {
            logger.debug("Session Did Activate SESSION_ID=[{}] -> {}",
                         sessionEvent.session.id, sessionEvent.source)
        }
    }

    override fun attributeAdded(sessionEvent: HttpSessionBindingEvent) {
        if (logger.isDebugEnabled) {
            logger.debug("Attribute Added SESSION_ID=[{}] -> {}={}",
                         sessionEvent.session.id, sessionEvent.name, sessionEvent.value)
        }
    }

    override fun attributeRemoved(sessionEvent: HttpSessionBindingEvent) {
        if (logger.isDebugEnabled) {
            logger.debug("Attribute Removed SESSION_ID=[{}] -> {}={}",
                         sessionEvent.session.id, sessionEvent.name, sessionEvent.value)
        }
    }

    override fun attributeReplaced(sessionEvent: HttpSessionBindingEvent) {
        if (logger.isTraceEnabled) {
            logger.trace("Attribute Replaced SESSION_ID=[{}] -> {}={}",
                         *arrayOf(sessionEvent.session.id, sessionEvent.name, sessionEvent.value))
        }
    }

    override fun sessionCreated(sessionEvent: HttpSessionEvent) {
        if (logger.isDebugEnabled) {
            logger.debug("Session Created SESSION_ID=[{}] -> {}",
                         sessionEvent.session.id, sessionEvent.source)
        }
    }

    override fun sessionDestroyed(sessionEvent: HttpSessionEvent) {
        if (logger.isDebugEnabled) {
            logger.debug("Session Destroyed SESSION_ID=[{}] -> {}",
                         sessionEvent.session.id, sessionEvent.source)
        }
    }
}