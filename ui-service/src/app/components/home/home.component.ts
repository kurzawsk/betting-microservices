import {Component, OnInit} from '@angular/core';
import {AuthService} from '../../services/auth.service';
import {Router} from '@angular/router';
import {HttpClient} from '@angular/common/http';

@Component({
  selector: 'app-home-component',
  templateUrl: './home.component.html'
})

export class HomeComponent implements OnInit {

  routeLinks: any[];
  activeLinkIndex = -1;
  protected logout = () => {
    console.log('Logout');
    this.authService.logout();
    this.router.navigate(['/']);

  }

  constructor(private http: HttpClient, private router: Router, public authService: AuthService) {
    this.routeLinks = [{
      label: 'Main',
      link: 'main',
      index: 0
    },
      {
        label: 'Matches',
        link: 'match',
        index: 1
      },
      {
        label: 'Teams',
        link: 'team',
        index: 2
      },
      {
        label: 'Mapping Cases',
        link: 'mapping-case',
        index: 3
      },
      {
        label: 'Reporting',
        link: 'reporting',
        index: 4
      },
      {
        label: 'Jobs',
        link: 'job',
        index: 5
      }
    ];
  }

  ngOnInit(): void {
    this.router.events.subscribe((res) => {
      this.activeLinkIndex = this.routeLinks.indexOf(this.routeLinks.find(tab => tab.link === this.router.url));
    });
    this.authService.refreshUserInfo();
  }
}
