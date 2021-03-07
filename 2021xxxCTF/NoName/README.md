# 总结
这题讲道理其实有点白给，主要考一个安卓开发的，自定义application是先执行的，attachbase，然后是oncreate这种顺序的，
习惯性先看mainfest.xml,发现没啥坑点，然后仔细观察有个自定义的application，发现是动态加载dex，而dex是由资源文件
中的enc文件，被解密后，加载的，利用frida进行dump,然后使用python打包成文件，逻辑还是挺简单的

# Frida exp
```
console.log("Script loaded successfully ");
Java.perform(function(){
        Java.use("dalvik.system.DexClassLoader").$init.implementation=function(arg1,arg2,arg3,arg4)
        {
            var ret=this.$init(arg1,arg2,arg3,arg4);
            console.log("YenKoc hooked!");
            console.log("arg1",arg1);
            console.log("arg2",arg2);
            console.log("arg3",arg3);
            console.log("arg4",arg4);
            send(arg4);
            return ret;
        }
        Java.use("javax.crypto.Cipher").doFinal.overload('[B').implementation=function(arg1)
        {
            var ret=this.doFinal(arg1);
            return ret;
        }
        Java.choose("com.d3ctf.noname.NoNameApp",{
            onMatch:function(instance)
            {
                console.log("found instance:",instance);
                console.log("getAESKey result:",JSON.stringify(instance.getAESKey()));
            },onComplete:function()
            {
                
            }
        })
    });
```
有一说一，我想一条龙的，直接从js dump出来，然后send到python，但是发现一个问题，时机找不准，要么就太早，
要么就太晚了，hook不到，蛮奇怪的，先放脚本先
```
import time
import frida
import base64
def dump(btarr):
    filename="1.bin"
    dex=open(filename,"wb")
    for x in btarr:
        dex.write(chr(x&0xff))
def my_message_handler(message,payload):
    print(message)
    print(payload)
    if message["type"]=="send":
        print(message["payload"])
        data=message["payload"].split(":")[1].strip()
device=frida.get_usb_device()
#device=frida.get_device_manager().add_remote_device("ip:port")
pid=device.spawn(["com.d3ctf.noname"])
time.sleep(1)
session=device.attach(pid)
time.sleep(1)

device.resume(pid)
#time.sleep(1)




#session=device.spawn(["com.d3ctf.noname"])
with open("test.js") as f:
    script=session.create_script(f.read())
script.on("message",my_message_handler)
script.load()
input()
```
