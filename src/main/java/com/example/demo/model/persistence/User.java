package com.example.demo.model.persistence;

import javax.persistence.*;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.Set;

@Getter
@Setter
@ToString
@Entity
@Table(name = "user")
public class User implements UserDetails {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@JsonProperty
	private long id;
	
	@Column(nullable = false, unique = true)
	@JsonProperty
	private String username;
	
	@OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "cart_id", referencedColumnName = "id")
	@JsonIgnore
	@ToString.Exclude
    private Cart cart;

	@JsonIgnore
	private boolean enabled = true;

	@JsonIgnore
	@ToString.Exclude
	private String password;

	@CreatedDate
	private LocalDateTime createdAt;
	@LastModifiedDate
	private LocalDateTime modifiedAt;

	@Override
	@JsonIgnore
	public Set<GrantedAuthority> getAuthorities() {return Collections.emptySet();}

	@Override
	@JsonIgnore
	public boolean isAccountNonExpired() {
		return enabled;
	}

	@Override
	@JsonIgnore
	public boolean isAccountNonLocked() {
		return enabled;
	}

	@Override
	@JsonIgnore
	public boolean isCredentialsNonExpired() {
		return enabled;
	}
}
