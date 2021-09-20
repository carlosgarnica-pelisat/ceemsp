import { Component, OnInit } from '@angular/core';
import {EmailValidator, FormBuilder, FormGroup, Validators} from "@angular/forms";
import {ActivatedRoute, Router} from "@angular/router";
import {AuthenticationService} from "../_services/authentication.service";
import {first} from "rxjs/operators";
import Credential from "../_models/Credential";

@Component({
  selector: 'app-login',
  templateUrl: './login.component.html',
  styleUrls: ['./login.component.css']
})
export class LoginComponent implements OnInit {

  loginForm: FormGroup | undefined;
  loading = false;
  submitted = false;
  returnUrl: string | undefined;

  private credential: Credential = new Credential();

  constructor(
    private formBuilder: FormBuilder,
    private route: ActivatedRoute,
    private router: Router,
    private authenticationService: AuthenticationService
  ) {
    if(this.authenticationService.currentUserValue) {
      this.router.navigate(['/']);
    }
  }

  ngOnInit() {
    this.loginForm = this.formBuilder.group({
      email: ['', Validators.required],
      password: ['', Validators.required]
    });

    this.returnUrl = this.route.snapshot.queryParams['returnUrl'] || '/';
  }

  get f() {
    return this.loginForm.controls;
  }

  /*verifyEmail() {
    let email = this.f.email.value;
    if(email === undefined || email === null) {
      return;
    }

    if(!EmailValidator.validate(email)) {
      console.warn("Given email is not valid");
      return;
    }

    this.commonService.checkEmailExistence(email).subscribe((response: PublicExists) => {
      console.log(response);
    }, () => {
      console.error("Error when the email was queried on database");
    })
  }*/

  login() {
    this.submitted = true;

    if(this.loginForm.invalid) {
      return;
    }

    this.loading = true;

    this.credential = new Credential();

    this.credential.email = this.f.email.value;
    this.credential.password = this.f.password.value;

    this.authenticationService.login(this.credential)
      .pipe(first())
      .subscribe((data) => {
        this.router.navigate([this.returnUrl]);
      }, (error) => {
        this.loading = false;
      })
  }

}
