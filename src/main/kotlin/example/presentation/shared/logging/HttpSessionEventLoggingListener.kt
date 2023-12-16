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
            logger.debug("SESSION_ID #{} sessionWillPassivate: {}",
                         sessionEvent.session.id,
                         sessionEvent.source)
        }
    }

    override fun sessionDidActivate(sessionEvent: HttpSessionEvent) {
        if (logger.isDebugEnabled) {
            logger.debug("SESSION_ID #{} sessionDidActivate: {}",
                         sessionEvent.session.id,
                         sessionEvent.source)
        }
    }

    override fun attributeAdded(sessionEvent: HttpSessionBindingEvent) {
        if (logger.isDebugEnabled) {
            logger.debug("SESSION_ID #{} attributeAdded: {}={}",
                         *arrayOf(sessionEvent.session.id,
                                  sessionEvent.name,
                                  sessionEvent.value))
        }
    }

    override fun attributeRemoved(sessionEvent: HttpSessionBindingEvent) {
        if (logger.isDebugEnabled) {
            logger.debug("SESSION_ID #{} attributeRemoved: {}={}",
                         *arrayOf(sessionEvent.session.id,
                                  sessionEvent.name,
                                  sessionEvent.value))
        }
    }

    override fun attributeReplaced(sessionEvent: HttpSessionBindingEvent) {
        if (logger.isTraceEnabled) {
            logger.trace("SESSION_ID #{} attributeReplaced: {}={}",
                         *arrayOf(sessionEvent.session.id,
                                  sessionEvent.name,
                                  sessionEvent.value))
        }
    }

    override fun sessionCreated(sessionEvent: HttpSessionEvent) {
        if (logger.isDebugEnabled) {
            logger.debug("SESSION_ID #{} sessionCreated: {}",
                         sessionEvent.session.id,
                         sessionEvent.source)
        }
    }

    override fun sessionDestroyed(sessionEvent: HttpSessionEvent) {
        if (logger.isDebugEnabled) {
            logger.debug("SESSION_ID #{} sessionDestroyed: {}",
                         sessionEvent.session.id,
                         sessionEvent.source)
        }
    }
}