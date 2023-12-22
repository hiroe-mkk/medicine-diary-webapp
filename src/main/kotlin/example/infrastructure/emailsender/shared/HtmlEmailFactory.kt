package example.infrastructure.emailsender.shared

import example.domain.shared.type.*
import org.springframework.stereotype.*

@Component
class HtmlEmailFactory(private val applicationProperties: ApplicationProperties) {
    fun createHeader(to: EmailAddress): Email.Header {
        return Email.Header(EmailAddress(applicationProperties.admin.emailAddress),
                            to,
                            "【${applicationProperties.name}】")
    }

    fun createBodyHeader(title: String): String {
        return buildString {
            append("<html><body>")
            append("<h3>${title}</h3><br/>")
        }
    }

    fun createBodyMain(lines: List<String>): String {
        return lines.joinToString("<br/>", prefix = "<div>", postfix = "</div><br/><br/>")
    }

    fun createBodyFooter(): String {
        return buildString {
            append("<div>")
            append("------------------------------------------------------------------------------------------<br/>")
            append("■ ご注意<br/>")
            append("※本メールにお心当たりがない場合は、誠に恐れ入りますが、破棄していただけますようお願い申し上げます。<br/>")
            append("※このメールは送信専用です。このメールにご返信いただいても対応いたしかねます。あらかじめご了承ください。<br/>")
            append("■ 配信<br/>")
            append("${applicationProperties.name}： ")
            append(createLink(applicationProperties.endpoint.web))
            append("</div>")
            append("</body></html>")
        }
    }

    private fun createLink(path: String): String = "<a href='${path}'>${path}</a>"
}