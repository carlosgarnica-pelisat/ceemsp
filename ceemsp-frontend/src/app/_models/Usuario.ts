export default class Usuario {
  jwtToken?: string;
  id: number;
  uuid: string;
  username: string;
  email: string;
  password: string;
  nombres: string;
  apellidos: string;
  apellidoMaterno: string;
  rol: string;
  usuario?: Usuario;
}
