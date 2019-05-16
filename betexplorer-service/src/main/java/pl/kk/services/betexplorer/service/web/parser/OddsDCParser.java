package pl.kk.services.betexplorer.service.web.parser;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Map;


@Component
public class OddsDCParser extends HtmlParser<Map<String, String[]>> {

    private final Odds1X2Parser delegate;

    @Autowired
    public OddsDCParser(Odds1X2Parser delegate) {
        this.delegate = delegate;
    }

    @Override
    public Map<String, String[]> parse(String content) {
        return delegate.parse(content);
    }
}
