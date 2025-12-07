package api.kitabu.uz.entity;

import api.kitabu.uz.enums.SmsStatus;
import api.kitabu.uz.enums.SmsType;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Table(name = "sms_history")
@Entity
public class SmsHistoryEntity extends BaseEntity {
    @Column(name = "sms_code")
    private String smsCode;
    @Column(name = "sms_type")
    private SmsType smsType;
    @Column(name = "status")
    private SmsStatus status;
    @Column(name = "phone")
    private String phone;
    @Column(name = "sms_text", columnDefinition = "text")
    private String smsText;
}
