// ✅ application.properties 설정값을 Java 객체로 바인딩하는 설정 클래스

package MobilLax.Config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "bus.api")
public class BusApiProperties {

    private String key;
    private Url url;

    public static class Url {
        private String ctycode;
        private String terminal;
        private String schedule;

        public String getCtycode() { return ctycode; }
        public void setCtycode(String ctycode) { this.ctycode = ctycode; }

        public String getTerminal() { return terminal; }
        public void setTerminal(String terminal) { this.terminal = terminal; }

        public String getSchedule() { return schedule; }
        public void setSchedule(String schedule) { this.schedule = schedule; }
    }

    public String getKey() { return key; }
    public void setKey(String key) { this.key = key; }

    public Url getUrl() { return url; }
    public void setUrl(Url url) { this.url = url; }
}
