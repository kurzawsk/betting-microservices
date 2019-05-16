import {Component, OnInit, ViewChild} from '@angular/core';
import {FormBuilder, FormGroup, Validators} from '@angular/forms';
import {AuthService} from '../../services/auth.service';
import {Router} from '@angular/router';
import {SpinnerContainerComponent} from '../common/progress-spinner/container/spinner-container.component';

@Component({
  selector: 'app-login-component',
  templateUrl: './login.component.html',
  styleUrls: ['./login.component.scss']
})
export class LoginComponent implements OnInit {

  isLoggedIn = false;
  loginForm: FormGroup;

  @ViewChild('loginSpinner')
  loginSpinner: SpinnerContainerComponent;

  constructor(private formBuilder: FormBuilder, private _service: AuthService, private router: Router) {
  }

  ngOnInit() {
    this.isLoggedIn = this._service.isAuthenticated();
    if (!this.isLoggedIn) {
      this.loginForm = this.formBuilder.group({
        username: ['', Validators.required],
        password: ['', Validators.required]
      });
    }
  }

  onSubmit(form) {
    this.loginSpinner.loadingFinishedSubj.next(false);
    this._service.login(form.username, form.password, () => {
      this.loginSpinner.loadingFinishedSubj.next(true);
      this.router.navigate(['main']);
    }, () => {
      this.loginSpinner.loadingFinishedSubj.next(true);
      alert('Wrong username or password');
    });
  }
}
