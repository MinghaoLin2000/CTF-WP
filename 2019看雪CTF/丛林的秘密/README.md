#  前沿: 这题出的是真好，出题人nb好把，学到了很多东西
1. jeb打开发现发现一个webview然后loadurl，当时隐隐感觉到了一些问题，但是没仔细查看，甚至我用frida invoke 那个gogogoJNI.sayHello方法的返回了一个127.0.0.1,我还是没发现什么端倪，毕竟是在本地服务器，什么东西都没有嘛
2. 结果我大意了，后面的那个什么按钮什么的，后面反编译so文件，发现比较函数只是不为空，就通过了，下意识这就是假的的，用来混淆人的，后面直接去找JNI_Onload函数里面去看，好家伙，果然有猫腻
```
int inti_proc()
{
  char *v0; // r0
  signed int v1; // r1
  signed int i; // r6
  struct addrinfo **v3; // r0
  int v4; // r4
  struct addrinfo *v5; // r5
  int result; // r0
  int arg; // [sp+8h] [bp-70h]
  int v8; // [sp+Ch] [bp-6Ch]
  int v9; // [sp+10h] [bp-68h]
  struct addrinfo *pai; // [sp+14h] [bp-64h]
  struct addrinfo req; // [sp+18h] [bp-60h]
  char v12[32]; // [sp+38h] [bp-40h]
  int v13; // [sp+58h] [bp-20h]

  v0 = &mm0;
  v1 = 34291;
  v9 = 1;
  while ( v1 )
  {
    --v1;
    *v0 ^= 0x67u;
    ++v0;
  }
  i = 1;
  *(_QWORD *)&req.ai_protocol = 0LL;
  *(_QWORD *)&req.ai_addr = 0LL;
  req.ai_family = 0;
  req.ai_flags = 1;
  req.ai_socktype = 1;
  req.ai_next = 0;
  if ( getaddrinfo(0, "8000", &req, &pai) )
    goto LABEL_19;
  v3 = &pai;
  i = 1;
  while ( 1 )
  {
    v5 = *v3;
    if ( !*v3 )
    {
      i = 2;
      goto LABEL_19;
    }
    v4 = socket(v5->ai_family, v5->ai_socktype, v5->ai_protocol);
    if ( v4 != -1 )
      break;
LABEL_10:
    v3 = &v5->ai_next;
  }
  if ( setsockopt(v4, 1, 2, &v9, 4u) == -1 )
    goto LABEL_19;
  if ( bind(v4, (const struct sockaddr *)v5->ai_canonname, v5->ai_addrlen) == -1 )
  {
    close(v4);
    goto LABEL_10;
  }
  freeaddrinfo(pai);
  if ( listen(v4, 128) == -1 )
  {
    i = 1;
  }
  else
  {
    for ( i = 0; i != 32; i += 4 )
    {
      arg = v4;
      v8 = 0;
      pthread_create((pthread_t *)&v12[i], 0, (void *(*)(void *))nullsub_, &arg);
    }
    sock_fd_g = v4;
  }
LABEL_19:
  result = _stack_chk_guard - v13;
  if ( _stack_chk_guard == v13 )
    result = i;
  return result;
}
```
先是smc了一块内存，然后创建了一个线程执行线程函数，跟进去
```
void __fastcall __noreturn nullsub_(int *a1)
{
  int *v1; // r4
  int v2; // r5
  char *v3; // r1
  size_t v4; // r0
  size_t v5; // r0
  char v6; // [sp+Eh] [bp-C422h]
  char buf; // [sp+C35Eh] [bp-D2h]
  socklen_t addr_len; // [sp+C38Ch] [bp-A4h]
  struct sockaddr addr; // [sp+C390h] [bp-A0h]

  v1 = a1;
  addr_len = 128;
  while ( 1 )
  {
    do
      v2 = accept(*v1, &addr, &addr_len);
    while ( v2 == -1 );
    v3 = &addr.sa_data[6];
    if ( addr.sa_family == 2 )
      v3 = &addr.sa_data[2];
    inet_ntop(addr.sa_family, v3, &buf, 0x2Eu);
    v4 = strlen(&mm0);
    snprintf(
      &v6,
      0xC350u,
      "HTTP/1.1 200 OK\r\nContent-Type: text/html; charset=utf-8\r\nContent-Length: %ld\r\n\r\n%s",
      v4,
      &mm0);
    v5 = strlen(&v6);
    if ( send(v2, &v6, v5, 0) == -1 )
      perror("send");
    close(v2);
  }
}
```
将那块内存的内容作为html内容发送出去了，前面又有listen的函数，我意识到了逻辑，他是监听了一波8080端口，他通过loadurl发送一个请求，而native函数作为一个服务端将响应发送出去，将真正的html传回来了，这里直接将内存dump出来，利用idc脚本，idapython一直报错，草
```
static main()
{
    auto i,fp;
    fp = fopen("C:\\Users\\mac\\Desktop\\dump\\dum1","wb");
    auto start = 0x4004;
    auto size =34291 ;
    for(i=start;i<start+size;i++)
    {
        fputc(Byte(i)^0x67,fp);
    }
    fp.close();
}
```
发现其实就是一个html文件，里面还加载了wasm，真正判断函数是wasm中的函数，这里的操作就是将wasm源码先转成c，然后再gcc编译成o文件，ida打开，发现是一个32元一次的方程和异或，
我这里卡在生成wasm文件，winhex操作了一波，出错，待解决，问下大佬