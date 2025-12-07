package api.kitabu.uz.entity;

import jakarta.persistence.*;
import lombok.*;
/*
 * @author Raufov Ma`ruf
 * bu nima uchun degan savolga javob
 * bitta user faqat bir postni bitta korganda oshadi
 * boshqa holatda yo`q getRemoteHost() ip bo`yicha checking
 * * */

@Data
@Entity
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "views")
public class ViewEntity{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    private String userIpAddress;
    private String postId;
}
