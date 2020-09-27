package life.majiang.community.controller;

import life.majiang.community.UserMapper;
import life.majiang.community.dto.AccessTokenDTO;
import life.majiang.community.dto.GithubUser;
import life.majiang.community.module.User;
import life.majiang.community.provider.GithubProvider;
import org.h2.util.CurrentTimestamp;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletRequest;
import java.util.UUID;

@Controller
public class AuthorizeController {
    @Autowired
    private GithubProvider githubProvider;

    @Value("${github.client.id}")
    private String clientID;
    @Value("${github.client.secret}")
    private String clientSecret;
    @Value("${github.redirect.uri}")
    private String redirectURI;

    @Autowired
    private UserMapper userMapper;

    @GetMapping("/callback")
    public String callback(@RequestParam(value = "code")String code,
                           @RequestParam(value = "state")String state,
                           HttpServletRequest request){
        AccessTokenDTO accessTokenDTO = new AccessTokenDTO();
        accessTokenDTO.setClient_id(clientID);
        accessTokenDTO.setClient_secret(clientSecret);
        accessTokenDTO.setCode(code);
        accessTokenDTO.setStata(state);
        accessTokenDTO.setRedirect_uri(redirectURI);
        //获取access_token
        String accessToken = githubProvider.getAccessToken(accessTokenDTO);
        //获取用户信息
        GithubUser githubUser = githubProvider.getUser(accessToken);
        System.out.println(githubUser.getLogin());
        if(githubUser != null){
            User user = new User();
            user.setToken(UUID.randomUUID().toString());
            user.setAccountId(String.valueOf(githubUser.getId()));
            user.setName(githubUser.getLogin());
            user.setGmtCreate(System.currentTimeMillis());
            user.setGmtModified(user.getGmtCreate());

            userMapper.insert(user);
            //登陆成功了,如果没有session就创建,如果有就获取
            request.getSession().setAttribute("user",githubUser);
            return "redirect:/";
        }else{
            //登陆失败,跳转到index
            return "redirect:/";
        }
    }
}
