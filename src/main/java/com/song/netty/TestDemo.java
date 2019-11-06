package com.song.netty;

import com.song.netty.decoder.Byte2IntegerReplayDecoder;
import com.song.netty.decoder.IntegerAddDecoder;
import com.song.netty.decoder.StringIntegerHeaderDecoder;
import com.song.netty.encoder.Integer2ByteEncoder;
import com.song.netty.encoder.String2IntegerEncoder;
import com.song.netty.handler.IntegerProcessHandler;
import com.song.netty.handler.StringProcessHandler;
import com.song.netty.protolcol.MsgProtos;
import io.netty.buffer.*;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.embedded.EmbeddedChannel;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import io.netty.handler.codec.LineBasedFrameDecoder;
import io.netty.handler.codec.string.StringDecoder;
import org.apache.commons.lang3.RandomUtils;
import org.junit.Test;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;

public class TestDemo {

    String content = "现在美国巅峰期结束了，美国基本上到东南亚只能给奖状";


    @Test
    public void testHandler() {

        InHandlerDemo inHandlerDemo = new InHandlerDemo();

        ChannelInitializer<EmbeddedChannel> channelInitializer = new ChannelInitializer<EmbeddedChannel>() {
            @Override
            protected void initChannel(EmbeddedChannel ch) throws Exception {
                ch.pipeline().addLast(inHandlerDemo);
            }
        };

        EmbeddedChannel channel = new EmbeddedChannel(channelInitializer);

        ByteBuf buf = Unpooled.buffer();

        buf.writeInt(1);

        channel.writeInbound(buf);

        channel.flush();

        channel.writeInbound(buf);

        channel.flush();

        channel.close();

        try {
            Thread.sleep(Integer.MAX_VALUE);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }


    @Test
    public void testInPipeline() {
        ChannelInitializer<EmbeddedChannel> initializer = new ChannelInitializer<EmbeddedChannel>() {
            @Override
            protected void initChannel(EmbeddedChannel ch) throws Exception {
                ch.pipeline().addLast(new InPipeline.SimpleInHandlerA());
                ch.pipeline().addLast(new InPipeline.SimpleInHandlerB());
                ch.pipeline().addLast(new InPipeline.SimpleInHandlerC());
            }
        };

        EmbeddedChannel channel = new EmbeddedChannel(initializer);

        ByteBuf buf = Unpooled.buffer();
        buf.writeInt(1);
        channel.writeInbound(buf);
        System.out.println("================================");
        channel.writeInbound(buf);


        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void testOutPipeline() {
        ChannelInitializer<EmbeddedChannel> initializer = new ChannelInitializer<EmbeddedChannel>() {
            @Override
            protected void initChannel(EmbeddedChannel ch) throws Exception {
                ch.pipeline().addLast(new OutPipeline.SimpleOutHandlerA());
                ch.pipeline().addLast(new OutPipeline.SimpleOutHandlerB());
                ch.pipeline().addLast(new OutPipeline.SimpleOutHandlerC());
            }
        };

        EmbeddedChannel channel = new EmbeddedChannel(initializer);

        ByteBuf buf = Unpooled.buffer();
        buf.writeInt(1);

        channel.writeOutbound(buf);

        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void testWriteRead() {
        ByteBuf buffer = ByteBufAllocator.DEFAULT.buffer(9, 100);

        System.out.println("动作： 分配ByteBuf(9, 100) " + buffer);

        buffer.writeBytes(new byte[]{1, 2, 3, 4});

        System.out.println("动作： 写入4个字节(1,2,3,4) " + buffer);

        while (buffer.isReadable()) {
            buffer.readByte();
        }

        System.out.println("动作： 取ByteBuf " + buffer);

        for (int i = 0; i < buffer.readableBytes(); i++) {
            buffer.getByte(i);
        }

        System.out.println("动作： 读完ByteBuf " + buffer);


    }

    @Test
    public void testRef() {

        ByteBuf buffer = ByteBufAllocator.DEFAULT.buffer();
        System.out.println("after create: " + buffer.refCnt());

        buffer.retain();
        System.out.println("after retain: " + buffer.refCnt());

        buffer.release();
        System.out.println("after release: " + buffer.refCnt());

        buffer.release();
        System.out.println("after release: " + buffer.refCnt());


        buffer.retain();
        System.out.println("after retain: " + buffer.refCnt());

        PooledByteBufAllocator.DEFAULT.directBuffer();

    }

    @Test
    public void showAlloc() {
        ByteBuf buffer = null;

        buffer = ByteBufAllocator.DEFAULT.buffer(9, 100);

        buffer = ByteBufAllocator.DEFAULT.buffer();

        buffer = ByteBufAllocator.DEFAULT.heapBuffer();

        buffer = ByteBufAllocator.DEFAULT.directBuffer();
    }

    @Test
    public void testHeapBuffer() {
        ByteBuf buf = ByteBufAllocator.DEFAULT.heapBuffer();

        buf.writeBytes("hello word".getBytes());

        if (buf.hasArray()) {
            byte[] array = buf.array();
            int offset = buf.arrayOffset() + buf.readerIndex();
            int length = buf.readableBytes();
            System.out.println(new String(array, offset, length));
        }
    }

    @Test
    public void testDirectBuffer() {
        ByteBuf buf = ByteBufAllocator.DEFAULT.directBuffer();

        buf.writeBytes("hello word".getBytes());

        if (!buf.hasArray()) {
            int length = buf.readableBytes();
            byte[] array = new byte[length];

            buf.getBytes(buf.readerIndex(), array);

            System.out.println(new String(array));
        }
    }

    @Test
    public void testBufComposite() {

        CompositeByteBuf buffer = ByteBufAllocator.DEFAULT.compositeBuffer();

        ByteBuf headerBuf = Unpooled.copiedBuffer("hello world".getBytes());

        ByteBuf bodyBuf = Unpooled.copiedBuffer("hello my world".getBytes());

        buffer.addComponents(headerBuf, bodyBuf);

        sendMsg(buffer);

        headerBuf.retain();

        buffer.release();

        buffer = ByteBufAllocator.DEFAULT.compositeBuffer();

        bodyBuf = Unpooled.copiedBuffer("this is hello world".getBytes());

        buffer.addComponents(headerBuf, bodyBuf);

        sendMsg(buffer);

        buffer.release();


    }

    @Test
    public void inttestBufComposite() {

        CompositeByteBuf buffer = ByteBufAllocator.DEFAULT.compositeBuffer();

        ByteBuf headerBuf = Unpooled.copiedBuffer("hello world".getBytes());

        ByteBuf bodyBuf = Unpooled.copiedBuffer("hello my world".getBytes());

        buffer.addComponents(headerBuf, bodyBuf);


        ByteBuffer nioBuffer = buffer.nioBuffer(0, 20);

        byte[] array = nioBuffer.array();
        System.out.print("byte = ");
        for (byte b : array) {
            System.out.print((char) b);
        }

        buffer.release();


    }


    @Test
    public void testSlice() {
        ByteBuf buf = ByteBufAllocator.DEFAULT.buffer(9, 100);

        System.out.println(buf);

        buf.writeBytes(new byte[]{1, 2, 3, 4});

        System.out.println(buf);

        ByteBuf slice = buf.slice();

        ByteBuf duplicate = buf.duplicate();

        System.out.println(slice);

        System.out.println(duplicate);
    }

    @Test
    public void testByte2IntegerDecoder() {

        EmbeddedChannel channel = new EmbeddedChannel(new ChannelInitializer<EmbeddedChannel>() {
            @Override
            protected void initChannel(EmbeddedChannel ch) throws Exception {
                ch.pipeline().addLast(new Byte2IntegerReplayDecoder());
                ch.pipeline().addLast(new IntegerProcessHandler());
            }
        });


        for (int i = 0; i < 10; i++) {
            ByteBuf buf = ByteBufAllocator.DEFAULT.buffer();
            buf.writeInt(i);
            channel.writeInbound(buf);

        }

        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void testIntegerAddDecoder() {

        EmbeddedChannel channel = new EmbeddedChannel(new ChannelInitializer<EmbeddedChannel>() {
            @Override
            protected void initChannel(EmbeddedChannel ch) throws Exception {
                ch.pipeline().addLast(new IntegerAddDecoder());
                ch.pipeline().addLast(new IntegerProcessHandler());
            }
        });


        for (int i = 0; i < 10; i++) {
            ByteBuf buf = ByteBufAllocator.DEFAULT.buffer();
            buf.writeInt(i);
            channel.writeInbound(buf);

        }

        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }


    @Test
    public void testStringDecoder() {

        String content = "现在美国巅峰期结束了，美国基本上到东南亚只能给奖状";

        EmbeddedChannel channel = new EmbeddedChannel(new ChannelInitializer<EmbeddedChannel>() {
            @Override
            protected void initChannel(EmbeddedChannel ch) throws Exception {
//                ch.pipeline().addLast(new StringReplayDecoder());
                ch.pipeline().addLast(new StringIntegerHeaderDecoder());
                ch.pipeline().addLast(new StringProcessHandler());
            }
        });

        byte[] bytes = content.getBytes(StandardCharsets.UTF_8);

        for (int j = 0; j < 100; j++) {
            int rand = RandomUtils.nextInt(1, 3);
            ByteBuf buf = ByteBufAllocator.DEFAULT.buffer();
            buf.writeInt(rand * bytes.length);

            for (int i = 0; i < rand; i++) {
                buf.writeBytes(bytes);
            }
            channel.writeInbound(buf);
        }


        try {
            Thread.sleep(10000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

    }

    @Test
    public void testLineBaseFrameDecoder() {


        EmbeddedChannel channel = new EmbeddedChannel(new ChannelInitializer<EmbeddedChannel>() {
            @Override
            protected void initChannel(EmbeddedChannel ch) throws Exception {
                ch.pipeline().addLast(new LineBasedFrameDecoder(1024));
                ch.pipeline().addLast(new StringDecoder());
                ch.pipeline().addLast(new StringProcessHandler());
            }
        });

        byte[] bytes = content.getBytes(StandardCharsets.UTF_8);

        for (int j = 0; j < 100; j++) {
            int rand = RandomUtils.nextInt(1, 3);
            ByteBuf buf = ByteBufAllocator.DEFAULT.buffer();
            for (int i = 0; i < rand; i++) {
                buf.writeBytes(bytes);
            }
            buf.writeBytes("\n".getBytes());
            channel.writeInbound(buf);
        }


        try {
            Thread.sleep(10000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

    }

    @Test
    public void testLengthFieldBaseFrameDecoder() {


        EmbeddedChannel channel = new EmbeddedChannel(new ChannelInitializer<EmbeddedChannel>() {
            @Override
            protected void initChannel(EmbeddedChannel ch) throws Exception {
                ch.pipeline().addLast(new LengthFieldBasedFrameDecoder(1024, 0, 4, 0, 4));
                ch.pipeline().addLast(new StringDecoder());
                ch.pipeline().addLast(new StringProcessHandler());
            }
        });


        for (int j = 0; j < 100; j++) {
            ByteBuf buf = ByteBufAllocator.DEFAULT.buffer();
            String s = j + "次发送->" + content;
            byte[] bytes = s.getBytes(StandardCharsets.UTF_8);
            buf.writeInt(bytes.length);
            buf.writeBytes(bytes);
            channel.writeInbound(buf);
        }


        try {
            Thread.sleep(10000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

    }


    private void sendMsg(CompositeByteBuf buf) {
        for (ByteBuf byteBuf : buf) {
            int length = byteBuf.readableBytes();
            byte[] array = new byte[length];
            byteBuf.getBytes(byteBuf.readerIndex(), array);
            System.out.print(new String(array));
        }

        System.out.println();
    }


    @Test
    public void testIntegerToByteDecoder() {

        EmbeddedChannel channel = new EmbeddedChannel(new ChannelInitializer<EmbeddedChannel>() {
            @Override
            protected void initChannel(EmbeddedChannel ch) throws Exception {
                ch.pipeline().addLast(new Integer2ByteEncoder());
            }
        });

        for (int i = 0; i < 100; i++) {
            channel.write(i);
        }
        channel.flush();

        ByteBuf buf = channel.readOutbound();
        while (buf != null) {
            System.out.println("o = " + buf.readInt());

            buf = channel.readOutbound();
        }

        try {
            Thread.sleep(10000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void testStringToIntegerDecoder() {

        EmbeddedChannel channel = new EmbeddedChannel(new ChannelInitializer<EmbeddedChannel>() {
            @Override
            protected void initChannel(EmbeddedChannel ch) throws Exception {
                ch.pipeline().addLast(new Integer2ByteEncoder());
                ch.pipeline().addLast(new String2IntegerEncoder());
            }
        });

        for (int i = 0; i < 100; i++) {
            channel.write("I am " + i);
        }
        channel.flush();

        ByteBuf buf = channel.readOutbound();
        while (buf != null) {
            System.out.println("o = " + buf.readInt());

            buf = channel.readOutbound();
        }

        try {
            Thread.sleep(10000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }


    @Test
    public void testProto() throws IOException {

        MsgProtos.Msg.Builder builder = MsgProtos.Msg.newBuilder();
        builder.setId(100);
        builder.setContent(content);

        MsgProtos.Msg message = builder.build();

        byte[] data = message.toByteArray();

        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        byteArrayOutputStream.write(data);
        data = byteArrayOutputStream.toByteArray();
        MsgProtos.Msg msg = MsgProtos.Msg.parseFrom(data);

        System.out.println("id = " + msg.getId());
        System.out.println("content = " + msg.getContent());


    }

    @Test
    public void testProtoDelimited() throws IOException {

        MsgProtos.Msg.Builder builder = MsgProtos.Msg.newBuilder();
        builder.setId(100);
        builder.setContent(content);

        MsgProtos.Msg message = builder.build();


        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();

        message.writeDelimitedTo(byteArrayOutputStream);

        ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(byteArrayOutputStream.toByteArray());

        MsgProtos.Msg msg = MsgProtos.Msg.parseDelimitedFrom(byteArrayInputStream);

        System.out.println("id = " + msg.getId());
        System.out.println("content = " + msg.getContent());


    }

}
