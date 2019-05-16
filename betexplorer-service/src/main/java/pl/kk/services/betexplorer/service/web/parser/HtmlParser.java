package pl.kk.services.betexplorer.service.web.parser;


public abstract class HtmlParser<T> {

    public abstract T parse(String content);

}
