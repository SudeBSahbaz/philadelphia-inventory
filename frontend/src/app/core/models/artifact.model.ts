export type ArtifactVisibility =
  | 'PUBLIC'
  | 'PRIVATE_FOR_CREW';

export interface ArtifactResponse {
  id: number;

  artifactCode: string;
  type: string | null;
  formNo: string | null;
  inventoryNo: string | null;
  studyNo: string | null;
  bagNo: string | null;
  boxNo: string | null;
  depth: string | null;
  box: string | null;

  findLocation: string | null;
  locality: string | null;
  sector: string | null;

  findDate: string | null;
  findYear: number | null;

  area: string | null;
  form: string | null;
  decorationType: string | null;
  pasteStructure: string | null;
  firing: string | null;
  technique: string | null;
  temper: string | null;
  temperAmount: string | null;
  slipStructure: string | null;
  angle: string | null;
  period: string | null;
  kind: string | null;
  munsell: string | null;
  diameter: string | null;
  weight: string | null;
  length: string | null;
  width: string | null;
  thickness: string | null;
  drawingNo: string | null;
  preservedPart: string | null;
  material: string | null;
  productionPlace: string | null;
  description: string | null;
  bibliography: string | null;

  visibility: ArtifactVisibility;

  createdById: number | null;
  createdByName: string | null;
  createdAt: string | null;

  updatedById: number | null;
  updatedByName: string | null;
  updatedAt: string | null;

  deleted: boolean;
}

export interface ArtifactListItemResponse {
  id: number;
  artifactCode: string;
  formNo: string | null;
  type: string | null;
  findLocation: string | null;
  sector: string | null;
  findDate: string | null;
  period: string | null;
  visibility: ArtifactVisibility;
  updatedByName: string | null;
  updatedAt: string | null;
  deleted: boolean;
}