import { Routes } from '@angular/router';


import { passwordChangeGuard } from './core/guards/password-change-guard';
import { roleGuard } from './core/guards/role-guard';


import { Login } from './features/auth/login/login';
import { ChangePassword } from './features/auth/change-password/change-password';
import { ForgotPassword } from './features/auth/forgot-password/forgot-password';
import { ResetPassword } from './features/auth/reset-password/reset-password';

import { Home } from './features/home/home';


import { Profile } from './features/profile/profile';


import { ArtifactDetail } from './features/artifacts/detail/artifact-detail';
import { ArtifactForm } from './features/artifacts/form/artifact-form';
import { ArtifactEdit } from './features/artifacts/artifact-edit/artifact-edit';
import { ArtifactHistory } from './features/artifacts/history/artifact-history';
import { ArtifactList } from './features/artifacts/list/artifact-list';
import { ArtifactSearch } from './features/artifacts/search/artifact-search';


import { UserList } from './features/users/list/user-list';
import { UserForm } from './features/users/form/user-form';
import { UserEdit } from './features/users/edit/user-edit';



export const routes: Routes = [


  {
    path: '',
    redirectTo: 'login',
    pathMatch: 'full'
  },


  {
    path: 'login',
    component: Login
  },


  // Şifresini unutan kullanıcı
  // giriş yapmadan erişebilmelidir.
  {
  path: 'forgot-password',
  component: ForgotPassword
},

{
  path: 'reset-password',
  component: ResetPassword
},



  // Şifresini değiştirmesi gereken kullanıcı
  // bu sayfaya erişebilmelidir.
  {
    path: 'change-password',
    component: ChangePassword
  },


  {
    path: 'home',
    component: Home,
    canActivate: [
      passwordChangeGuard
    ]
  },



  // --------------------------------------------------
  // PROFİL / HESAP
  // --------------------------------------------------


  {
    path: 'profile',
    component: Profile,
    canActivate: [
      passwordChangeGuard
    ]
  },



  // --------------------------------------------------
  // KULLANICI YÖNETİMİ
  // SADECE ADMIN
  // --------------------------------------------------


  {
    path: 'users',
    component: UserList,
    canActivate: [
      passwordChangeGuard,
      roleGuard
    ],
    data: {
      roles: ['ADMIN']
    }
  },


  {
    path: 'users/new',
    component: UserForm,
    canActivate: [
      passwordChangeGuard,
      roleGuard
    ],
    data: {
      roles: ['ADMIN']
    }
  },


  {
    path: 'users/:userId/edit',
    component: UserEdit,
    canActivate: [
      passwordChangeGuard,
      roleGuard
    ],
    data: {
      roles: ['ADMIN']
    }
  },



  // --------------------------------------------------
  // BULUNTULAR
  // --------------------------------------------------


  {
    path: 'artifacts',
    component: ArtifactList,
    canActivate: [
      passwordChangeGuard
    ]
  },


  {
    path: 'artifacts/search',
    component: ArtifactSearch,
    canActivate: [
      passwordChangeGuard
    ]
  },


  {
    path: 'artifacts/new',
    component: ArtifactForm,
    canActivate: [
      passwordChangeGuard,
      roleGuard
    ],
    data: {
      roles: [
        'ADMIN',
        'CREW_MEMBER'
      ]
    }
  },


  {
    path: 'artifacts/:artifactCode/edit',
    component: ArtifactEdit,
    canActivate: [
      passwordChangeGuard,
      roleGuard
    ],
    data: {
      roles: [
        'ADMIN',
        'CREW_MEMBER'
      ]
    }
  },


  {
    path: 'artifacts/:artifactId/history',
    component: ArtifactHistory,
    canActivate: [
      passwordChangeGuard,
      roleGuard
    ],
    data: {
      roles: [
        'ADMIN',
        'CREW_MEMBER'
      ]
    }
  },


  {
    path: 'artifacts/:artifactCode',
    component: ArtifactDetail,
    canActivate: [
      passwordChangeGuard
    ]
  },



  // --------------------------------------------------
  // BİLİNMEYEN ROUTE
  // --------------------------------------------------


  {
    path: '**',
    redirectTo: 'login'
  }


];