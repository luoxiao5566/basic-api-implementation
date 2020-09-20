package com.thoughtworks.rslist.po;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import javax.validation.constraints.*;
import java.util.List;

@Entity
@Table(name = "user")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UserPo {
    @Id
    @GeneratedValue
    private int id;
    @Column(name = "name")
    @NotNull
    @Size(max = 8)
    private String name;
    @NotNull
    private String gender;
    @Min(18)
    @Max(100)
    private int age;
    @Email
    private String email;
    @Pattern(regexp = "1\\d{10}")
    private String phone;
    private int voteNum = 10;

    @OneToMany(cascade = CascadeType.REMOVE,mappedBy = "userPo")
    private List<RsEventPo> rsEventPos;

    @OneToMany
    private List<VotePo> votePos;



    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
}
