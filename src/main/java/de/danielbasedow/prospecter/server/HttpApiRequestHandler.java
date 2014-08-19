package de.danielbasedow.prospecter.server;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.sun.tools.internal.xjc.reader.xmlschema.bindinfo.BIConversion;
import de.danielbasedow.prospecter.core.*;
import de.danielbasedow.prospecter.core.document.Document;
import de.danielbasedow.prospecter.core.document.MalformedDocumentException;
import de.danielbasedow.prospecter.core.schema.Schema;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.*;
import io.netty.util.CharsetUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

public class HttpApiRequestHandler extends SimpleChannelInboundHandler<Object> {
    private static final ObjectMapper mapper = new ObjectMapper();

    private final Instance instance;
    private FullHttpRequest request;
    private static final Logger LOGGER = LoggerFactory.getLogger(HttpApiRequestHandler.class);

    public HttpApiRequestHandler(Instance instance) {
        this.instance = instance;
    }

    private final StringBuilder responseBuffer = new StringBuilder();

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, Object msg) throws Exception {
        if (msg instanceof FullHttpRequest) {
            this.request = (FullHttpRequest) msg;
        } else {
            throw new Exception();
        }
        FullHttpResponse response = dispatch();

        ctx.writeAndFlush(response).addListener(ChannelFutureListener.CLOSE);
    }

    protected FullHttpResponse dispatch() throws UndefinedIndexFieldException, MalformedQueryException, MalformedDocumentException, JsonProcessingException {
        String uri = request.getUri();
        String[] uriParts = uri.split("/");
        if (uriParts.length == 0) {
            LOGGER.warn("root uri specified, please supply a valid uri! Doing nothing!");
            return new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.INTERNAL_SERVER_ERROR);
        }

        String schemaName = uriParts[1];
        LOGGER.debug("method: " + request.getMethod() + ", schema: '" + schemaName + "'");
        if (request.getMethod() == HttpMethod.POST && schemaName != null) {
            return matchDocument(instance.getSchema(schemaName));
        }

        if (request.getMethod() == HttpMethod.PUT && schemaName != null) {
            return addQuery(instance.getSchema(schemaName));
        }
        if (request.getMethod() == HttpMethod.DELETE && schemaName != null) {
            String queryId = uriParts[1];
            return deleteQuery(instance.getSchema(schemaName), queryId);
        }
        return null;
    }

    protected DefaultFullHttpResponse addQuery(Schema schema) throws MalformedQueryException, UndefinedIndexFieldException {
        ByteBuf content = request.content();
        if (content.isReadable()) {
            schema.addQuery(content.toString(CharsetUtil.UTF_8));
        }
        return new DefaultFullHttpResponse(
                HttpVersion.HTTP_1_1,
                HttpResponseStatus.OK,
                Unpooled.copiedBuffer("", CharsetUtil.UTF_8)
        );
    }

    protected FullHttpResponse matchDocument(Schema schema) throws MalformedDocumentException, JsonProcessingException {
        ByteBuf content = request.content();
        int matchCount = 0;
        ObjectNode node = mapper.getNodeFactory().objectNode();

        if (content.isReadable()) {
            LOGGER.info("start matching");
            Document doc = schema.getDocumentBuilder().build(content.toString(CharsetUtil.UTF_8));
            Matcher matcher = schema.matchDocument(doc);
            ArrayNode results = node.putArray("matches");
            for (Query query : matcher.getMatchedQueries()) {
                matchCount++;
                ObjectNode queryNode = results.addObject();
                queryNode.put("id", query.getQueryId());
            }
            LOGGER.info("finished matching");
        }
        node.put("count", matchCount);
        return new DefaultFullHttpResponse(
                HttpVersion.HTTP_1_1,
                HttpResponseStatus.OK,
                Unpooled.copiedBuffer(mapper.writeValueAsString(node), CharsetUtil.UTF_8)
        );
    }

    protected DefaultFullHttpResponse deleteQuery(Schema schema, String queryId) throws MalformedQueryException, UndefinedIndexFieldException {
        if (queryId == null || "".equals(queryId)) {
            LOGGER.warn("No query id supplied in DELETE request.");
            return new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.INTERNAL_SERVER_ERROR);
        }

        Long qid = Long.parseLong(queryId, 10);

        schema.deleteQuery(qid);
        return new DefaultFullHttpResponse(
                HttpVersion.HTTP_1_1,
                HttpResponseStatus.OK,
                Unpooled.copiedBuffer("", CharsetUtil.UTF_8)
        );
    }
}
