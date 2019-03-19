package cn.pompip.netty;

import cn.pompip.utils.Res;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.http.*;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import io.netty.handler.codec.http.websocketx.WebSocketServerProtocolHandler;
import io.netty.handler.stream.ChunkedFile;
import io.netty.handler.stream.ChunkedWriteHandler;

import java.time.LocalDateTime;

import static io.netty.handler.codec.http.HttpHeaders.Names.CONTENT_LENGTH;

public class Main {
    public static void main(String[] args) {
        try {
            new ServerBootstrap()
                    .group(new NioEventLoopGroup(), new NioEventLoopGroup())
                    .channel(NioServerSocketChannel.class)
                    .childHandler(new MyChannel())
                    .bind(8000).sync();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    static class MyChannel extends ChannelInitializer<NioSocketChannel> {

        @Override
        protected void initChannel(NioSocketChannel ch) throws Exception {
//            ch.pipeline().addLast(new HttpServerCodec())
//                    .addLast(new LoggingHandler(LogLevel.INFO))

            ch.pipeline()
                    .addLast(new ChunkedWriteHandler())
                            .addLast("decoder", new HttpRequestDecoder())
                    .addLast("encoder", new HttpResponseEncoder())
                    .addLast(new HttpObjectAggregator(8192))
                    .addLast(new WebSocketServerProtocolHandler("/ws"))

//                    .addLast("httpServerCodec", new HttpServerCodec())
                    .addLast(new SimpleChannelInboundHandler<HttpRequest>() {
                        @Override
                        protected void channelRead0(ChannelHandlerContext ctx, HttpRequest msg) throws Exception {
//                            System.out.println(msg.uri());
//                            System.out.println(msg.method());
                            System.out.println(msg.getClass());
                            ChunkedFile file = new ChunkedFile(Res.get("/web/index.html"));
                            DefaultFullHttpResponse response = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1,
                                    HttpResponseStatus.OK);

                            response.headers().set(HttpHeaderNames.CONTENT_TYPE, "text/html;charset=UTF-8");
//                            response.headers().set(HttpHeaderNames.CONTENT_LENGTH, response.content().readableBytes());
                            response.headers().set(HttpHeaderNames.CONNECTION, HttpHeaderValues.KEEP_ALIVE);
                            response.headers().add(HttpHeaderNames.CONTENT_LENGTH, file.length());
                            ctx.write(response);
                            ctx.write(file);

                            ChannelFuture future = ctx.writeAndFlush(LastHttpContent.EMPTY_LAST_CONTENT);

                            future.addListener(ChannelFutureListener.CLOSE);



                        }

                        @Override
                        public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
                            super.channelReadComplete(ctx);
                            System.out.println("channelReadComplete");

                        }
                    })
                    .addLast(new SimpleChannelInboundHandler<TextWebSocketFrame>() {
                        @Override
                        protected void channelRead0(ChannelHandlerContext ctx, TextWebSocketFrame msg) throws Exception {
                            System.out.println(msg.text());
                            ctx.channel().writeAndFlush(new TextWebSocketFrame("服务器时间: " + LocalDateTime.now()));
                        }


                    });
        }
    }
}
